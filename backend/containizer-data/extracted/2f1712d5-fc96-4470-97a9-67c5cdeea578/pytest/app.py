from flask import Flask, render_template, request

app = Flask(__name__)

@app.route("/", methods=["GET", "POST"])
def home():
    message = ""
    if request.method == "POST":
        name = request.form.get("name")
        feedback = request.form.get("feedback")
        message = f"Thanks {name}! Your feedback was received."

        # Real-world note:
        # Here is where you would save to DB or send to another service

    return render_template("index.html", message=message)

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000, debug=True)
