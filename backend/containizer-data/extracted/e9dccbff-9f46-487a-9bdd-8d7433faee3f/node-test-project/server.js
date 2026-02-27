const express = require("express");
const path = require("path");
const app = express();

// serve static frontend
app.use(express.static(path.join(__dirname, "public")));

// simple API endpoint
app.get("/api/info", (req, res) => {
  res.json({ status: "ok", message: "Node is working!" });
});

// listen on port 3000
const port = 3000;
app.listen(port, () => {
  console.log("Server running on port", port);
});
