import { useState, useEffect } from "react";
import { DuckData } from "../types/duck";

export function useDuck() {
  const [duck, setDuck] = useState<DuckData | null>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  const fetchDuck = async () => {
    setLoading(true);
    setError(null);
    try {
      const response = await fetch("http://localhost:8080/api/duck");
      if (!response.ok) {
        throw new Error("Could not reach the Duck API. Is the backend running?");
      }
      const data: DuckData = await response.json();
      setDuck(data);
    } catch (err) {
      setError(err instanceof Error ? err.message : "An unexpected error occurred.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchDuck();
  }, []);

  return { duck, loading, error, fetchDuck };
}
