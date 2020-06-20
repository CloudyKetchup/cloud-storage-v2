import React, { FC, CSSProperties, useContext } from "react";

import FileClient from "../../api/FileClient";

import { ThemeContext, Theme }  from "../../context/ThemeContext";
import { DirectoryContext }     from "../../context/DirectoryContext";
import { FilesContext }         from "../../context/FilesContext";

import { ReactComponent as UploadSvg } from "../../assets/icons/upload.svg";

import "./upload-button.css";

type IProps = {
  className?  : string
  style?      : CSSProperties
};

const UploadButton: FC<IProps> = ({ className, style }) =>
{
  const { theme }   = useContext(ThemeContext);
  const { folder }  = useContext(DirectoryContext);
  const { addFile } = useContext(FilesContext);
  const fileClient  = FileClient.instance();

  const onClick = async () =>
  {
    const input = document.getElementById("file-upload-input") as HTMLInputElement;

    input.click()
  };

  const onUpload = async () =>
  {
    const input = document.getElementById("file-upload-input") as HTMLInputElement;

    const file = input.files?.item(0);

    if (file)
    {
      const formData = new FormData();

      formData.append("file", file);

      await upload(formData);
    }
  };

  const upload = async (formData: FormData) =>
  {
    if (folder)
    {
      const { data, error } = await fileClient.upload(formData, folder.id);

      data && addFile(data);

      //TODO: error notification
    }
  };

  const darkTheme = {
    background: Theme.DARK,
    color: "white"
  };

  const lightTheme = {
    background: Theme.LIGHT,
    color: "#181818"
  };

  const defaultStyle = theme == Theme.DARK ? darkTheme : lightTheme;

  const svgFill = theme == Theme.LIGHT ? "black" : "white";

  return (
    <div >
      <input
        id="file-upload-input"
        style={{ display: "none" }}
        type="file"
        onChange={onUpload}
      />
      <button
        id="upload-button"
        onClick={onClick}
        className={className}
        style={style || defaultStyle}
      >
        <div>
          <UploadSvg
            fill={svgFill}
            width="15px"
            height="15px"
            style={{ marginTop: "10px" }}
          />
        </div>
        <div>
          <span>Upload</span>
        </div>
      </button>
    </div>
  );
};

export default UploadButton;