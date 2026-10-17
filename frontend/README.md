# InternFlow Frontend

Next.js (App Router) + React + TypeScript + Tailwind CSS.

This directory holds the InternFlow frontend. Only a minimal placeholder page
exists so far — no application features or role-based interfaces are implemented.

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
