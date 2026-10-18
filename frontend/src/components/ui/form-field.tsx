import type { ComponentPropsWithRef, ReactNode } from "react";

export interface FormFieldProps
  extends Omit<ComponentPropsWithRef<"input">, "id"> {
  /** Unique id for the input; also the target of the label's htmlFor. */
  id: string;
  /** Visible label text. Always rendered (no placeholder-only labels). */
  label: string;
  /** Error message. When present it is linked to the input via aria-describedby. */
  error?: string;
  /** Optional supporting hint, also linked via aria-describedby. */
  hint?: ReactNode;
}

const inputBaseStyles =
  "w-full rounded-md border bg-surface px-3 py-2 text-base text-foreground " +
  "placeholder:text-foreground/50 " +
  "focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-primary " +
  "disabled:cursor-not-allowed disabled:opacity-60";

const inputErrorBorder = "border-danger";
const inputIdleBorder = "border-border-strong";

export function FormField({
  id,
  label,
  error,
  hint,
  required,
  className,
  ...inputProps
}: FormFieldProps) {
  const hintId = `${id}-hint`;
  const errorId = `${id}-error`;
  const describedBy =
    [error ? errorId : null, hint ? hintId : null]
      .filter((value): value is string => value !== null)
      .join(" ") || undefined;

  return (
    <div className={className}>
      <label htmlFor={id} className="mb-1.5 block text-sm font-medium text-foreground">
        {label}
        {required ? (
          <span aria-hidden="true" className="ml-0.5 text-danger">
            *
          </span>
        ) : null}
      </label>
      <input
        id={id}
        required={required}
        aria-invalid={error ? true : undefined}
        aria-describedby={describedBy}
        className={`${inputBaseStyles} ${
          error ? inputErrorBorder : inputIdleBorder
        }`}
        {...inputProps}
      />
      {hint ? (
        <p id={hintId} className="mt-1.5 text-sm text-foreground/70">
          {hint}
        </p>
      ) : null}
      {error ? (
        <p id={errorId} className="mt-1.5 text-sm text-danger">
          {error}
        </p>
      ) : null}
    </div>
  );
}
