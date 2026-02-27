import { Link } from "react-router-dom";

export default function Landing() {
  return (
    <div className="flex flex-col items-center justify-center text-center py-32 space-y-6">
      <h1 className="text-4xl font-bold tracking-tight">
        Turn ZIP Files into Running Containers
      </h1>

      <p className="text-lg text-zinc-300 max-w-xl">
        Containerizer lets you upload a project ZIP and instantly run it as a Docker container with
        CPU / Memory / Volume customization.
      </p>

      <Link
        to="/register"
        className="px-5 py-2 bg-blue-600 hover:bg-blue-700 rounded-md text-white transition"
      >
        Get Started
      </Link>
    </div>
  );
}
