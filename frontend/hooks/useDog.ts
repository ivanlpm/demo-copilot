import { useState, useEffect } from "react";
import { DogData } from "../types/dog";

export function useDog() {
  const [dog, setDog] = useState<DogData | null>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  const fetchDog = async () => {
    setLoading(true);
    setError(null);
    try {
      const apiUrl = process.env.NEXT_PUBLIC_DOG_API_URL || "http://localhost:8080/api/dog";
      const response = await fetch(apiUrl);
      if (!response.ok) {
        throw new Error("Could not reach the Dog API");
      }
      const data: DogData = await response.json();
      setDog(data);
    } catch (err) {
      setError(err instanceof Error ? err.message : "An unexpected error occurred.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchDog();
  }, []);

  return { dog, loading, error, fetchDog };
}
