import React, { useContext, useEffect, useState, FC } from "react";

import FetchErrorBanner from "../FetchErrorBanner/FetchErrorBanner";
import Loading          from "../LoadingComponent/LoadingComponent";

import { Folder } from "../../models/Directory";

import { FilesContext }     from "../../context/FilesContext";
import { FoldersContext }   from "../../context/FoldersContext";

import FolderClient from "../../api/FolderClient";

import "./directory-content.css";

type IProps = { folder : Folder };

const DirectoryContent: FC<IProps> = ({ folder }) =>
{
  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError]     = useState<boolean>(false);
  
  const { setFiles }   = useContext(FilesContext);
  const { setFolders } = useContext(FoldersContext);

  const folderClient = FolderClient.instance();

  useEffect(() =>
  {
    const fetchItems = async () =>
    {
      setLoading(true);

      const { data, error } = await folderClient.getContent(folder.id);

      setLoading(false);

      if (data)
      {
        setFiles(data.files);
        setFolders(data.folders);
      } else if (error)
      {
        setError(true);
      }
    };

    fetchItems();
  }, []);

  return (
    <div className="directory-content">
      <Loading
        loading={loading}
        error={error}
        fallback={<FetchErrorBanner text="Failed fetching folder content" height="calc(100% - 20px)"/>}
        style={{ height : "calc(100% - 20px)" }}
      >
          {/* // TODO: render files and folders here */}
      </Loading>
    </div>
  );
};

export default DirectoryContent;