export interface LoadingIndicatorProps {
  /** Visible label shown next to the spinner (e.g. "Signing in…"). */
  label: string;
  className?: string;
}

/**
 * A purely visual spinner with a visible text label. The spinner itself is
 * hidden from assistive technology.
 *
 * Announcement is intentionally NOT handled here: a live region must exist in
 * the DOM *before* its text changes to be reliably announced, so callers that
 * need the loading state announced should render a persistent `role="status"`
 * region and update its text (see the sign-in form). This component stays
 * presentational so it can be reused without duplicate announcements.
 */
export function LoadingIndicator({ label, className }: LoadingIndicatorProps) {
  return (
    <span
      className={`inline-flex items-center gap-2 text-sm text-foreground/70${
        className ? ` ${className}` : ""
      }`}
    >
      <svg
        aria-hidden="true"
        viewBox="0 0 24 24"
        fill="none"
        className="size-4 animate-spin motion-reduce:animate-none"
      >
        <circle
          cx="12"
          cy="12"
          r="10"
          stroke="currentColor"
          strokeWidth="4"
          className="opacity-25"
        />
        <path
          d="M12 2a10 10 0 0 1 10 10"
          stroke="currentColor"
          strokeWidth="4"
          strokeLinecap="round"
        />
      </svg>
      {label}
    </span>
  );
}
