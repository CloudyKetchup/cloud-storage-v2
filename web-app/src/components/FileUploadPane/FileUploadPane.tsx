import React, { useContext } from "react";
import { useHistory } from "react-router";

import FileUpload from "./FileUpload";

import { ThemeContext, Theme }	from "../../context/ThemeContext";
import { FileUploadContext } 		from "../../context/FileUploadContext";

import { ReactComponent as ClipboardSvg } from "../../assets/icons/clipboard.svg";
import { ReactComponent as CloseSvg } 		from "../../assets/icons/delete.svg";

import "./file-upload-pane.css";

const FileUploadPane = () =>
{
	const { theme } 	= useContext(ThemeContext);
	const { uploads, removeAll } = useContext(FileUploadContext);
	const history 		= useHistory();

	const allFinished = (): boolean => uploads.filter(upload => !upload.finished).length === 0;

	const onClose = () =>
	{
		const div = document.getElementById(`file-upload-pane`);

		if (div)
		{
			div.style.opacity = "0";
			div.style.top = "0";
		}
		setTimeout(() =>
		{
			if (allFinished())
			{
				removeAll();
			}
			history.push("/")
		}, 100);
	};

	return (
		<div
			id="file-upload-pane"
			className={`file-upload-pane ${ theme === Theme.DARK ? "file-upload-pane-dark" : "" }`}
		>
			<div>
				<span>File Upload</span>
				<div className="file-upload-pane-close" onClick={onClose}>
					<CloseSvg />
				</div>
			</div>
			<div className="file-upload-items">
			{
				uploads.length > 0
				?
				<div className="file-upload-list">
					{uploads.map(upload => <FileUpload key={upload.id} data={upload} />)}
				</div>
				:
				<div className="file-upload-empty-pane">
					<div>
						<ClipboardSvg />
					</div>
					<div>
						<h5>Empty here</h5>
					</div>
				</div>
			}
			</div>
		</div>
	);
};

export default FileUploadPane;