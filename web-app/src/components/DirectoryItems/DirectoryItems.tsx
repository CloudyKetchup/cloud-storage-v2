import React, { useContext } from "react";

import { CompactSeparator } from "../CompactSeparator/CompactSeparator";
import EmptyDirectory				from "../EmptyDirectory/EmptyDirectory";

import { FilesContext } 	from "../../context/FilesContext";
import { FoldersContext } from "../../context/FoldersContext";

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
				{ /* Render items if present */ }
				<div className="directory-items">
					{
						// render files if list not empty
						files.length > 0
						&&
						<>
							<CompactSeparator/>
							<div>
								<ItemsViewHeader title="Files"/>
								<div>
									{}
								</div>
							</div>
						</>
					}
					{
						// render folders if list not empty
						folders.length > 0
						&&
						<>
							<CompactSeparator/>
							<div>
								<ItemsViewHeader title="Folders"/>
								<div>
								{}
								</div>
							</div>
						</>
					}
				</div>
			}
		</>
	);
};

export default DirectoryItems;