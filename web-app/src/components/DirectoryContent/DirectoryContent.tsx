import React, { useContext, useEffect, useState, FC } from "react";

import FetchErrorBanner from "../FetchErrorBanner/FetchErrorBanner";
import Loading          from "../LoadingComponent/LoadingComponent";
import DirectoryItems   from "../DirectoryItems/DirectoryItems";

import FolderClient         from "../../api/FolderClient";
import { Folder }           from "../../models/Directory";

import { FilesContext }       from "../../context/FilesContext";
import { FoldersContext }     from "../../context/FoldersContext";
import { ClipboardProvider }  from "../../context/ClipboardContext";

import "./directory-content.css";

type IProps = { folder : Folder };

const DirectoryContent: FC<IProps> = ({ folder }) =>
{
  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError]     = useState<boolean>(false);
  
  const { setFiles }   = useContext(FilesContext);
  const { setFolders } = useContext(FoldersContext);

  const folderClient = FolderClient.instance();

  const onFetchError = () => setError(true);

  useEffect(() =>
  {
    const fetchItems = async () =>
    {
      setLoading(true);

<<<<<<< HEAD
      const { data, error } = await folderClient.getContent(folder.id);
      
      setLoading(false);
=======
      const content = await folderClient.getContent(folder.id, onFetchError);
>>>>>>> master

      if (content)
      {
        setFiles(content.files);
        setFolders(content.folders);
      }
      setLoading(false);
    };

    fetchItems();
  }, [folder]);

  return (
    <div className="directory-content">
      <ClipboardProvider>
        <Loading
          loading={loading}
          error={error}
          fallback={<FetchErrorBanner text="Failed fetching folder content" height="calc(100% - 90px)" />}
          style={{ height: "calc(100% - 90px)" }}
        >
          <DirectoryItems />
        </Loading>
      </ClipboardProvider>
    </div>
  );
};

export default DirectoryContent;