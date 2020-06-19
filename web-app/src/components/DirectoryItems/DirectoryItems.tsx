import React, { useContext } from "react";

import { CompactSeparator } from "../CompactSeparator/CompactSeparator";
import EmptyDirectory				from "../EmptyDirectory/EmptyDirectory";
import FolderItem 					from "../FolderItem/FolderItem";
import FileItem 						from "../FileItem/FileItem";

import { FilesContext } 				from "../../context/FilesContext";
import { FoldersContext } 			from "../../context/FoldersContext";
import { ContextMenuProvider } 	from "../../context/ContextMenuContext";

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
									<ItemsViewHeader title="Files"/>
									<div className="file-items-grid">
										{files.flatMap(f => <FileItem key={f.id} data={f}/>)}
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
		</>
	);
};

export default DirectoryItems;