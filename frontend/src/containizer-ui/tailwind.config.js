/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,jsx,ts,tsx}",
  ],
  safelist: [
    "bg-zinc-900",
    "text-white"
  ],
  theme: {
    extend: {},
  },
  plugins: [],
}
