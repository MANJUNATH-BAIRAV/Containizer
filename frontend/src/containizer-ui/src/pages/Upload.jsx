import { useState } from "react";
import axios from "axios";

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? "";

export default function Upload() {
  const [file, setFile] = useState(null);
  const [cpu, setCpu] = useState("");
  const [memory, setMemory] = useState("");
  const [volume, setVolume] = useState("");
  const [result, setResult] = useState("");

  const handleSubmit = async () => {
    if (!file) return alert("Select a zip file first");

    const form = new FormData();
    form.append("file", file);

    if (cpu) form.append("cpu", cpu);
    if (memory) form.append("memory", memory);
    if (volume) form.append("volume", volume);

    try {
      const res = await axios.post(
        `${API_BASE_URL}/api/upload`,
        form
      );

      setResult(res.data);
    } catch (err) {
      const message = err?.response?.data || err?.message || "Upload failed";
      alert(message);
      console.error(err);
    }
  };

  return (
    <div className="px-6 py-10 max-w-xl mx-auto">
      <h2 className="text-3xl font-semibold mb-6">Upload & Customize</h2>

      <div className="mb-6">
        <label className="block mb-2">Select ZIP file:</label>
        <input
          type="file"
          accept=".zip"
          onChange={(e) => setFile(e.target.files[0])}
          className="bg-neutral-800 px-3 py-2 rounded w-full"
        />
      </div>

      <div className="mb-6 space-y-4">
        <div>
          <label className="block mb-1">CPU (e.g. 0.5 or 1):</label>
          <input
            placeholder="Optional"
            className="bg-neutral-800 px-3 py-2 rounded w-full"
            onChange={(e) => setCpu(e.target.value)}
          />
        </div>

        <div>
          <label className="block mb-1">Memory (e.g. 512m or 1g):</label>
          <input
            placeholder="Optional"
            className="bg-neutral-800 px-3 py-2 rounded w-full"
            onChange={(e) => setMemory(e.target.value)}
          />
        </div>

        <div>
          <label className="block mb-1">Volume (e.g. /host:/container):</label>
          <input
            placeholder="Optional"
            className="bg-neutral-800 px-3 py-2 rounded w-full"
            onChange={(e) => setVolume(e.target.value)}
          />
        </div>
      </div>

      <button
        onClick={handleSubmit}
        className="px-6 py-3 bg-green-600 hover:bg-green-700 rounded"
      >
        Containerize
      </button>

      {result && (
        <div className="mt-6 bg-neutral-800 p-4 rounded">
          <p>Result:</p>
          <pre className="whitespace-pre-wrap">{result}</pre>
        </div>
      )}
    </div>
  );
}
