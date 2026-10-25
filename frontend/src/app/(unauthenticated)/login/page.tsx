import type { Metadata } from "next";

import { SignInForm } from "@/features/auth/sign-in-form";

export const metadata: Metadata = {
  title: "Sign in",
  description: "Sign in to InternFlow.",
};

export default function SignInPage() {
  return (
    <main className="flex w-full flex-1 flex-col items-center justify-center px-4 py-12 sm:px-6">
      <div className="w-full max-w-sm sm:max-w-md">
        <h1 className="text-center text-2xl font-semibold tracking-tight text-foreground sm:text-3xl">
          InternFlow
        </h1>
        <p className="mt-2 text-center text-sm text-foreground/70">
          Sign in with the account issued by your college administrator.
        </p>

        <div className="mt-8 rounded-xl border border-border bg-surface p-6 shadow-sm sm:p-8">
          <h2 className="mb-6 text-lg font-medium text-foreground">
            Sign in to your account
          </h2>
          <SignInForm />
        </div>

        <p className="mt-6 text-center text-xs leading-5 text-foreground/60">
          Accounts are created by a College Administrator. There is no
          self-registration in InternFlow.
        </p>
      </div>
    </main>
  );
}
