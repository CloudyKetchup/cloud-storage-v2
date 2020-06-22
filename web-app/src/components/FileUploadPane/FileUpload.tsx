import React, { FC, useState, useEffect, useContext } from "react";

import { UploadFile } 					from "../../context/FileUploadContext";
import { ThemeContext, Theme } 	from "../../context/ThemeContext"; 

import { ReactComponent as FileSvg } from "../../assets/icons/file.svg";

import "./file-upload.css";

type IProps = { data : UploadFile };

const FileUpload: FC<IProps> = ({ data }) =>
{
	const { theme }								= useContext(ThemeContext);
	const [progress, setProgress] = useState<string>(data.progress);
	const [error, setError] 			= useState<boolean>(data.error);

	useEffect(() =>
	{
		data.onProgress.subscribe(progress =>
		{
			setProgress(progress);
		});

		data.onError.subscribe(error =>
		{
			setError(error);
		});
	}, [])

	return (
		<div
			className={`file-upload
				${ theme === Theme.DARK && "file-upload-dark" }
				${ error && (theme === Theme.DARK ? "file-uplaod-error-dark" : "file-upload-error") }
			`}
		>
			<div>
				<div className="file-upload-icon">
					<FileSvg />
				</div>
				<div>
					{data.name.substring(0, 10)}
				</div>
			</div>
			<div style={{ width : `${progress?.substring(0, 3)}%` }}/>
		</div>
	);
}

export default FileUpload;