import React, { createContext, FC, useState } from "react";

import { Folder, File } from "../models/Directory";

export enum ClipbaordItemAction { COPY, MOVE };

export type ClipboardItem = {
    body    : File | Folder,
    action  : ClipbaordItemAction
};

type IContext = {
    item?   : ClipboardItem
    setItem : (item?: ClipboardItem) => void
    clearClipboard : () => void
};

const ClipboardContext = createContext<IContext>({ 
    setItem : (_item?: ClipboardItem) => {},
    clearClipboard : () => {}
});

const ClipboardConsumer = ClipboardContext.Consumer

const ClipboardProvider: FC = props => {
    const [item, setItem] = useState<ClipboardItem>();

    const clear = () => setItem(undefined);
    
    return (
        <ClipboardContext.Provider value={{ item, setItem, clearClipboard: clear }}>
            {props.children}
        </ClipboardContext.Provider>
    );
};

export { ClipboardContext, ClipboardConsumer, ClipboardProvider };