import React, { FC, useState, createContext } from "react";

import { WithId } from "../models/Directory";

import { Subject } from 'rxjs';
import { findById, deleteById } from "../utils/array.utils";

export interface UploadFile extends WithId
{
	name 				: string
	progress 		: string
	onProgress 	: Subject<string>
	error 			: boolean,
	onError 		: Subject<boolean>,
	finished 		: boolean
};

type IContext = {
	uploads 			: UploadFile[]
	addUpload 		: (file: UploadFile) => void
	removeUpload 	: (id: string) => void
	removeAll			: () => void
};

const FileUploadContext = createContext<IContext>({
	uploads 			: [],
	addUpload 		: (_file: UploadFile) => {},
	removeUpload 	: (_id: string) => {},
	removeAll 		: () => {}
});

const FileUploadConsumer = FileUploadContext.Consumer;

const FileUploadProvider: FC = ({ children }) =>
{
	const [uploads, setUploads] = useState<UploadFile[]>([]);

	const addUpload = (file: UploadFile) =>
	{
		if (!findById<UploadFile>(uploads, file.id))
		{
			uploads.push(file);

			setUploads([...uploads]);
		}
	};

	const removeUpload = (id: string) =>
	{
		deleteById<UploadFile>(uploads, id);

		setUploads([...uploads]);
	};

	const removeAll = () =>
	{
		uploads.length = 0;

		setUploads([...uploads]);
	}
	
	return (
		<FileUploadContext.Provider value={{ uploads, addUpload, removeUpload, removeAll }}>
			{children}
		</FileUploadContext.Provider>
	);
};

export { FileUploadContext, FileUploadConsumer, FileUploadProvider };