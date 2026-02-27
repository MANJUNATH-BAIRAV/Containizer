import { useState } from "react";
import { useNavigate } from "react-router-dom";

export default function Login() {
  const navigate = useNavigate();
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");

  const handleLogin = (e) => {
    e.preventDefault();

    const saved = JSON.parse(localStorage.getItem("user"));
    if (!saved) return alert("User not found. Please register first.");

    if (saved.username === username && saved.password === password) {
      localStorage.setItem("loggedIn", "true");
      navigate("/upload");
    } else {
      alert("Invalid credentials");
    }
  };

  return (
    <div className="flex flex-col items-center justify-center h-[80vh] px-6">
      <h2 className="text-2xl font-semibold mb-6">Login</h2>

      <form onSubmit={handleLogin} className="flex flex-col gap-4 w-full max-w-xs">
        <input
          className="px-3 py-2 bg-neutral-800 rounded"
          placeholder="Username"
          onChange={(e) => setUsername(e.target.value)}
          required
        />
        <input
          className="px-3 py-2 bg-neutral-800 rounded"
          placeholder="Password"
          type="password"
          onChange={(e) => setPassword(e.target.value)}
          required
        />

        <button
          type="submit"
          className="px-4 py-2 bg-blue-600 hover:bg-blue-700 rounded"
        >
          Login
        </button>
      </form>
    </div>
  );
}
