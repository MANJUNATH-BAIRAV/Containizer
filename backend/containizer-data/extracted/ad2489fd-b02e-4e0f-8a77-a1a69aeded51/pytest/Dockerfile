FROM python:3.11-slim

WORKDIR /app

# Copy project files
COPY . /app

# Install dependencies if requirements.txt exists
RUN if [ -f requirements.txt ]; then pip install --no-cache-dir -r requirements.txt; fi

EXPOSE 5000

CMD ["python", "app.py"]
