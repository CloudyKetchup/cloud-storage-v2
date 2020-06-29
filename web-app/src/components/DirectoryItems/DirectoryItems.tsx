import React, { useContext } from "react";

import { File } from "../../models/Directory";

import { CompactSeparator } from "../CompactSeparator/CompactSeparator";
import EmptyDirectory				from "../EmptyDirectory/EmptyDirectory";
import FolderItem 					from "../FolderItem/FolderItem";
import FileItem 						from "../FileItem/FileItem";
import Clipboard						from "./Clipboard";

import { FilesContext } 				from "../../context/FilesContext";
import { FoldersContext } 			from "../../context/FoldersContext";
import { ContextMenuProvider } 	from "../../context/ContextMenuContext";
import { ClipboardContext  } 		from "../../context/ClipboardContext";

import "./directory-items.css";

const ItemsViewHeader = ({ title } : { title : string }) => (
	<div className="items-view-header">
		<div>
			<h2>{title}</h2>
		</div>
	</div>
);

const DirectoryItems = () =>
{
	const { files } 	= useContext(FilesContext);
	const { folders } = useContext(FoldersContext);
	const { item }  	= useContext(ClipboardContext);

	const isImage = (file: File): boolean =>
	{
		switch (file.extension)
		{
			case "jpeg":
			case "jpg":
			case "png":
			case "gif":
				return true;
			default:
				return false;
		}
	};

	return (
		<>
			{
				// if no files or folders present render empty directory
				files.length === 0 && folders.length === 0
				?
				<EmptyDirectory text="Empty here"/>
				:
				<div className="directory-items">
					<ContextMenuProvider>
						{
							// render files if list not empty
							files.length > 0
							&&
							<>
								<CompactSeparator/>
								<div>
									<ItemsViewHeader title="Images"/>
									<div className="image-items-grid">
										{files.filter(isImage).flatMap(f => <FileItem key={f.id} data={f} image={true}/>)}
									</div>
								</div>
								<div>
									<ItemsViewHeader title="Files"/>
									<div className="file-items-grid">
										{files.filter(f => !isImage(f)).flatMap(f => <FileItem key={f.id} data={f}/>)}
									</div>
								</div>
							</>
						}
						{
							// render folders if list not empty
							folders.length > 0
							&&
							<>
								<div>
									<ItemsViewHeader title="Folders"/>
									<div className="folder-items-grid">
										{folders.flatMap(f => <FolderItem key={f.id} data={f}/>)}
									</div>
								</div>
							</>
						}
					</ContextMenuProvider>
				</div>
			}
			{item && <Clipboard item={item}/>}
		</>
	);
};

export default DirectoryItems;