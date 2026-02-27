import { useState } from "react";
import { useNavigate } from "react-router-dom";

export default function Register() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const navigate = useNavigate();

  const handleRegister = (e) => {
    e.preventDefault();

    localStorage.setItem("user", JSON.stringify({ username, password }));
    alert("Registration successful!");
    navigate("/login");
  };

  return (
    <div className="flex flex-col items-center justify-center h-[80vh] px-6">
      <h2 className="text-2xl font-semibold mb-6">Register</h2>

      <form onSubmit={handleRegister} className="flex flex-col gap-4 w-full max-w-xs">
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
          Register
        </button>
      </form>
    </div>
  );
}
