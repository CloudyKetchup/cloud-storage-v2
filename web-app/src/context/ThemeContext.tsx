import React, { createContext, FC, useState } from "react";

export enum Theme
{
  LIGHT = "FFFFFF",
  DARK  = "#181818"
};

type IContext = {
  theme: Theme,
  setTheme: (theme: Theme) => void
};

const ThemeContext = createContext<IContext>({
  theme: Theme.LIGHT,
  setTheme: (_theme: Theme) => {}
});

const ThemeConsumer = ThemeContext.Consumer

const ThemeProvider: FC = props =>
{
  const [theme, setTheme] = useState<Theme>(Theme.LIGHT)

	return (
		<ThemeContext.Provider value={{ theme, setTheme }}>
			{props.children}
		</ThemeContext.Provider>
	);
}

export { ThemeContext, ThemeProvider, ThemeConsumer };