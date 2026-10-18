import Link from "next/link";

export default function Home() {
  return (
    <main className="flex flex-1 flex-col items-center justify-center gap-6 px-6 py-24 text-center">
      <h1 className="text-4xl font-semibold tracking-tight sm:text-5xl">
        InternFlow
      </h1>
      <p className="max-w-2xl text-lg leading-8 text-foreground/70">
        Internship operations and compliance, from registration to verified
        completion — in one centralized, traceable workflow.
      </p>
      <p className="max-w-xl text-sm text-foreground/60">
        The development foundation is in progress. Role-based dashboards,
        authentication, and application features are not implemented yet.
      </p>
      <Link
        href="/login"
        className="mt-2 inline-flex items-center rounded-md bg-primary px-4 py-2 text-sm font-medium text-on-primary transition-colors hover:bg-primary-hover focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-primary"
      >
        Go to sign in
      </Link>
    </main>
  );
}
