import React, { createContext, FC, useState } from "react";

type IContext = {
	menuId?: string
	setMenuId: (id: string) => void
};

const ContextMenuContext = createContext<IContext>({
	setMenuId: (_id : string) => {}
});

const ContextMenuConsumer = ContextMenuContext.Consumer;

const ContextMenuProvider: FC = ({ children }) =>
{
	const [menuId, setMenuId] = useState<string>();

	return (
		<ContextMenuContext.Provider value={{ menuId, setMenuId }}>
			{children}
		</ContextMenuContext.Provider>
	);
};

export { ContextMenuContext, ContextMenuConsumer, ContextMenuProvider };