# Frontend Development Instructions

Guidelines for Next.js 16, React 19, and Tailwind CSS 4 development.

## ⚛️ React & Next.js Patterns
- **Next.js 16 App Router**: 
  - Default to **Server Components** for data fetching.
  - Use `"use client"` only for components requiring interactivity (state, effects, event listeners).
  - Use standard file names: `page.tsx`, `layout.tsx`, `loading.tsx`, `error.tsx`.
- **React 19 Hooks**:
  - Leverage `use` for consuming promises or context where appropriate.
  - Logic for fetching should be encapsulated in **Custom Hooks** (e.g., `hooks/useDuck.ts`).
- **Components**: 
  - Keep components small and focused. 
  - Shared UI components should be in `components/ui/`.

## 🎨 Styling (Tailwind CSS 4)
- **Utilities First**: Use Tailwind utility classes directly in `className`.
- **Consistency**: Follow the project's color palette and spacing.
- **Conditional Classes**: Use libraries like `clsx` or `tailwind-merge` if complex condition logic is needed.

## ⌨️ TypeScript Usage
- **Interfaces over Types**: Use `interface` for object definitions that might be extended, and `type` for unions/aliases.
- **Strict Typing**: Avoid `any`. Explicitly type component props using `interface Props { ... }`.
- **API Responses**: Define interfaces for all API response bodies in the `types/` folder.

## 📡 Data Fetching
- **Native Fetch**: Use the native `fetch` API.
- **Error Handling**: Always wrap fetch calls in try/catch or handle the response status to avoid silent failures.
- **Loading States**: Implement Skeleton screens or loading indicators using Next.js `loading.tsx` or local state.

## 🏗️ Project Structure
- `app/`: Routing and layout.
- `components/`: Modular UI units (split into `ui/`, `layout/`, etc.).
- `hooks/`: Business logic and state management.
- `types/`: Global TypeScript definitions.
- `public/`: Static assets.
