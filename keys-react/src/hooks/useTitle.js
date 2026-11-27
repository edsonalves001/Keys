import { useEffect } from "react";

export default function useTitle(titulo) {
  useEffect(() => {
    document.title = titulo;
  }, [titulo]);
}
