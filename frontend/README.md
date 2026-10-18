# InternFlow Frontend

Next.js (App Router) + React + TypeScript + Tailwind CSS.

## Current status (Stage 2.2)

Two routes exist:

- `/` — neutral landing page with a link to sign in.
- `/login` — sign-in interface (email + password) with client-side
  validation, loading state, and accessible error presentation.

The login form is **not connected to a backend**: no approved authentication
API contract exists yet (SAD §10; SRS-IF-01), so submitting the form shows an
explicit integration-pending notice and no request is sent. There is no
registration flow, no Company Mentor role, no session handling, and no
role-based dashboards.

## Structure

```
src/
  app/
    layout.tsx                     # Root layout (Geist fonts, metadata)
    page.tsx                       # Neutral landing page
    (unauthenticated)/login/       # Sign-in route (route group, SAD §7)
  components/ui/                   # Shared UI: Button, FormField, Alert, LoadingIndicator
  features/auth/                   # Auth UI: sign-in form
  lib/api/                         # Typed API client (base URL, generic request)
  lib/auth/                        # Auth integration boundary (sign-in seam)
```

`lib/api/client.ts` owns base-URL configuration and generic HTTP mechanics.
Presentational components never call `fetch` directly (SAD §7). The client
currently defines no endpoints; the API Specification will.

## Commands

```powershell
npm install      # install dependencies
npm run dev      # start development server (http://localhost:3000)
npm run build    # production build
npm run start    # run the production build
npm run lint     # ESLint
npm run typecheck # TypeScript check (tsc --noEmit)
```

Environment variables: copy `.env.example` to `.env.local` and fill in values.
`.env.local` is git-ignored and must never be committed.

Full setup instructions: see [`../docs/development/SETUP.md`](../docs/development/SETUP.md).
