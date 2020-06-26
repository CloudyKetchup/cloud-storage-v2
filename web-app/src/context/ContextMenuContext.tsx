import React, { createContext, FC, useState, useEffect } from "react";

type IContext = {
	menuId?: string,
	setMenuId: (id: string | undefined) => void
};

const ContextMenuContext = createContext<IContext>({
	setMenuId: (_id : string | undefined) => {}
});

const ContextMenuConsumer = ContextMenuContext.Consumer;

const ContextMenuProvider: FC = ({ children }) =>
{
	const [menuId, setMenuId] = useState<string>();

	useEffect(() =>
	{
		window.onclick = () => setMenuId("");
	}, []);

	return (
		<ContextMenuContext.Provider value={{ menuId, setMenuId }}>
			{children}
		</ContextMenuContext.Provider>
	);
};

export { ContextMenuContext, ContextMenuConsumer, ContextMenuProvider };