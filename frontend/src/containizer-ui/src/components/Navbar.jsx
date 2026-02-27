import { Link, useNavigate } from "react-router-dom";

export default function Navbar() {
  const loggedIn = localStorage.getItem("loggedIn") === "true";
  const navigate = useNavigate();

  const handleLogout = () => {
    localStorage.removeItem("loggedIn");
    navigate("/login");
  };

  return (
    <nav className="w-full px-6 py-4 bg-neutral-800 flex justify-between items-center">
      <Link to="/" className="text-xl font-semibold">
        Containizer
      </Link>

      <div className="flex gap-4 items-center">
        {!loggedIn && (
          <>
            <Link to="/login" className="hover:text-blue-400">Login</Link>
            <Link to="/register" className="hover:text-blue-400">Register</Link>
          </>
        )}

        {loggedIn && (
          <>
            <Link to="/upload" className="hover:text-blue-400">Upload</Link>
            <button 
              onClick={handleLogout}
              className="text-red-400 hover:text-red-300"
            >
              Logout
            </button>
          </>
        )}
      </div>
    </nav>
  );
}
