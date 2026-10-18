import type { ReactNode } from "react";

export interface AlertProps {
  /** Semantic tone. "error" announces immediately; "info" is announced politely. */
  tone?: "error" | "info";
  children: ReactNode;
  className?: string;
}

const toneStyles: Record<NonNullable<AlertProps["tone"]>, string> = {
  error: "border-danger bg-danger-surface text-danger",
  info: "border-border-strong bg-surface-muted text-foreground",
};

/**
 * A short message shown to the user. Errors use role="alert" so they are
 * announced as soon as they appear; informational messages use role="status".
 */
export function Alert({ tone = "error", children, className }: AlertProps) {
  return (
    <div
      role={tone === "error" ? "alert" : "status"}
      className={`rounded-md border px-3 py-2 text-sm ${toneStyles[tone]}${
        className ? ` ${className}` : ""
      }`}
    >
      {children}
    </div>
  );
}
