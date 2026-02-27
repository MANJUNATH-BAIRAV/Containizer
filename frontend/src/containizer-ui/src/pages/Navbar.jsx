import { Link } from "react-router-dom";

export default function Navbar() {
  return (
    <div className="flex justify-between items-center px-6 py-4 border-b border-zinc-700">
      <Link to="/" className="text-xl font-semibold">
        Containerizer
      </Link>

      <div className="space-x-4">
        <Link to="/login" className="hover:text-blue-400">Login</Link>
        <Link to="/register" className="hover:text-blue-400">Register</Link>
      </div>
    </div>
  );
}
