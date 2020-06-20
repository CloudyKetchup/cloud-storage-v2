import React, { useContext } from "react";
import { Route, useHistory } from "react-router";

import DirectoryInfo    from "../DirectoryInfoHeader/DirectoryInfoHeader";
import DirectoryContent from "../DirectoryContent/DirectoryContent";
import FetchErrorBanner from "../FetchErrorBanner/FetchErrorBanner";
import Loading          from "../LoadingComponent/LoadingComponent";
import TextSubmitModal  from "../TextSubmitModal/TextSubmitModal";
import ModalShadow      from "../ModalShadow/ModalShadow";

import { AppContext }       from "../../context/AppContext";
import { DirectoryContext } from "../../context/DirectoryContext";
import { FoldersContext }   from "../../context/FoldersContext";

import FolderClient from "../../api/FolderClient";

import "./main-content.css";

const MainContent = () =>
{
  const { loading, errorLoadingApp } = useContext(AppContext);
  const { folder }    = useContext(DirectoryContext);
  const { addFolder } = useContext(FoldersContext);
  const history       = useHistory();
  const folderClient  = FolderClient.instance();

  const onFolderCreate = async (folderName: string) =>
  {
    if (folder)
    {
      const { data, error } = await folderClient.create(folder, folderName);

      data && addFolder(data);

      history.push("/");
    }
  };

  return (
    <div className="main-content">
      <DirectoryInfo folder={folder} />
      <Loading
        loading={loading}
        error={errorLoadingApp}
        fallback={<FetchErrorBanner text="Error happened" height="calc(100% - 90px)"/>}
        style={{ height: "calc(100% - 90px)" }}
      >
        {folder && <DirectoryContent folder={folder}/>}
      </Loading>
      <Route path="/folder/create" component={() =>
        <ModalShadow>
          <TextSubmitModal
            title="Create new folder"
            buttonText="Create"
            placeholder="New folder name"
            onSubmit={onFolderCreate}
            onClose={() => history.push("/")}
          />
        </ModalShadow>
      }/>
    </div>
  );
};

export default MainContent;