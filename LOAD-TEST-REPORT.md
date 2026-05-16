# Load Testing Report

## Tool Used

- k6

---

## Test Scenario

- Endpoint tested:
  `/actuator/health`

- Virtual Users (VUs):
  10

- Test Duration:
  15 seconds

---

## Results

- Total Requests:
  150

- Average Response Time:
  2.59 ms

- 95th Percentile:
  7.26 ms

- Requests/sec:
  9.94 req/s

---

## Observations

Load testing was executed successfully using k6.

The application handled concurrent requests successfully during the test duration.

Performance metrics including latency and throughput were collected successfully.