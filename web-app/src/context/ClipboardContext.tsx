import React, { createContext, FC, useState } from "react";
import { Folder, File } from "../models/Directory";

type IContext = {
    item?   : File | Folder
    setItem : (item?: File | Folder) => void
};

const ClipboardContext = createContext<IContext>({ 
    setItem : (_item?: File | Folder) => {}
});

const ClipboardConsumer = ClipboardContext.Consumer

const ClipboardProvider: FC = props => {
    const [item, setItem] = useState<File | Folder>();
    
    return (
        <ClipboardContext.Provider value={{ item, setItem }}>
            {props.children}
        </ClipboardContext.Provider>
    );
};

export { ClipboardContext, ClipboardConsumer, ClipboardProvider };