/**
 * Minimal typed API client foundation (Stage 2.2).
 *
 * Scope: this module owns base-URL resolution and generic HTTP mechanics only.
 * It defines NO authentication endpoints, request bodies, or response shapes,
 * because no approved API Specification exists yet (SAD §10: the endpoint
 * catalogue and payload schemas are a future deliverable; SRS-IF-01).
 *
 * Deferred integration work (see docs/reviews/STAGE-2.2-REVIEW.md §8):
 * - Endpoint paths, request/response contracts, and failure-category mapping
 *   (SRS-IF-04) belong to the API Specification substage.
 * - Cookie/credentials behavior for cross-origin deployment is deliberately
 *   NOT set here. Server-managed sessions (IF-DEC-19) require the deployment
 *   topology (IF-DEC-10) and cookie SameSite/Secure configuration to be
 *   resolved before `credentials` or CORS assumptions are coded.
 */

/** Categories this client can produce. Mirrors the technical/config parts of
 *  FRD §7.1 failure categories at the transport level only. */
export type ApiFailureKind =
  /** No base URL configured in NEXT_PUBLIC_API_BASE_URL. */
  | "configuration-missing"
  /** fetch rejected (DNS, offline, CORS preflight, aborted). */
  | "network"
  /** Server answered with a non-2xx status. No response body is parsed for
   *  user-facing messages until the error shape is specified. */
  | "http";

export class ApiError extends Error {
  readonly kind: ApiFailureKind;
  /** Present for kind === "http". */
  readonly status?: number;

  constructor(kind: ApiFailureKind, message: string, status?: number) {
    super(message);
    this.name = "ApiError";
    this.kind = kind;
    this.status = status;
  }
}

export function getApiBaseUrl(): string {
  const baseUrl = process.env.NEXT_PUBLIC_API_BASE_URL;
  if (!baseUrl) {
    throw new ApiError(
      "configuration-missing",
      "NEXT_PUBLIC_API_BASE_URL is not configured.",
    );
  }
  // Tolerate a trailing slash so path joining is predictable.
  return baseUrl.replace(/\/+$/, "");
}

export interface RequestOptions {
  method?: "GET" | "POST" | "PUT" | "PATCH" | "DELETE";
  /** JSON-serializable body. Omitted for GET/DELETE by convention. */
  body?: unknown;
  signal?: AbortSignal;
}

/**
 * Perform a JSON request against the backend.
 *
 * Returns parsed JSON on 2xx. Throws {@link ApiError} for configuration,
 * network, or HTTP failures; the Error.message is safe for logs but callers
 * must NOT display it (or any technical detail) directly to users — present a
 * generic message instead and use `kind`/`status` only for logging.
 *
 * This function is intentionally unused by the Stage 2.2 login UI: there is
 * no approved authentication endpoint to call yet. It exists so later
 * substages extend one client rather than spreading fetch calls.
 */
export async function apiRequest<T>(
  path: string,
  options: RequestOptions = {},
): Promise<T> {
  const baseUrl = getApiBaseUrl(); // throws ApiError("configuration-missing")
  const url = `${baseUrl}${path.startsWith("/") ? path : `/${path}`}`;

  const hasBody = options.body !== undefined;
  let response: Response;
  try {
    response = await fetch(url, {
      method: options.method ?? "GET",
      headers: hasBody ? { "Content-Type": "application/json" } : undefined,
      body: hasBody ? JSON.stringify(options.body) : undefined,
      signal: options.signal,
      // NOTE: `credentials` is intentionally left at the fetch default
      // ("same-origin"). Crossing origins to a session cookie requires a
      // resolved deployment topology (IF-DEC-10) and cookie policy
      // (IF-DEC-19, Secure in production, SameSite) — neither is fixed yet.
    });
  } catch (cause) {
    // Network-level failure. Do not expose underlying detail to callers'
    // user interfaces; it is attached for logging only.
    throw new ApiError(
      "network",
      `Network request to ${path} failed: ${
        cause instanceof Error ? cause.message : String(cause)
      }`,
    );
  }

  if (!response.ok) {
    throw new ApiError(
      "http",
      `Request to ${path} returned HTTP ${response.status}.`,
      response.status,
    );
  }

  // 204/empty bodies: treat as undefined rather than failing to parse.
  if (response.status === 204) {
    return undefined as T;
  }
  try {
    return (await response.json()) as T;
  } catch {
    throw new ApiError(
      "http",
      `Request to ${path} returned an unparseable body.`,
      response.status,
    );
  }
}
