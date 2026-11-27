import { createContext, useCallback, useContext, useEffect, useState } from "react";

const ThemeCtx = createContext(null);

const temaInicial = () => {
  try {
    return (
      localStorage.getItem("keys-theme") ||
      (matchMedia("(prefers-color-scheme: dark)").matches ? "dark" : "light")
    );
  } catch (e) {
    return "light";
  }
};

export function ThemeProvider({ children }) {
  const [theme, setTheme] = useState(temaInicial);

  useEffect(() => {
    document.documentElement.dataset.theme = theme;
    try {
      localStorage.setItem("keys-theme", theme);
    } catch (e) {}
  }, [theme]);

  const alternar = useCallback(() => setTheme((t) => (t === "dark" ? "light" : "dark")), []);

  return <ThemeCtx.Provider value={{ theme, alternar }}>{children}</ThemeCtx.Provider>;
}

export const useTheme = () => useContext(ThemeCtx);
