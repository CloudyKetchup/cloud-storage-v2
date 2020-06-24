import { API_URL } from "./env.config";

import { StorageStats }     from "../models/StorageStats";
import { Folder }           from "../models/Directory";
import { DirectoryContent } from "../models/DirectoryContent";
import { ApiResponse }      from "../models/ApiResponse";

import axios from "axios";
import { FolderMoveData } from "../models/MoveData";

class FolderClient
{
  private static inst: FolderClient | undefined; // client instance
  private URL = `${API_URL}/folder`;

  private constructor() {}

  static instance = () : FolderClient =>
  {
    if (!FolderClient.inst)
    {
      FolderClient.inst = new FolderClient();
    }
    return FolderClient.inst;
  }

  getRoot = () : Promise<ApiResponse<Folder>> =>
  (
    axios.get(`${this.URL}/root`)
      .then(response => ({ data : response.data }))
      .catch(e => ({ error : e }))
  );

  getRootStats = () : Promise<ApiResponse<StorageStats>> =>
  (
    axios.get(`${this.URL}/root/stats`)
      .then(response => ({ data : response.data }))
      .catch(e => ({ error : e }))
  );

  getContent = (id: string) : Promise<ApiResponse<DirectoryContent>> =>
  (
    axios.get(`${this.URL}/content?id=${id}`)
      .then(response => ({ data : response.data }))
      .catch(e => ({ error : e }))
  )

  getPreviousFolders = (id: string) : Promise<ApiResponse<Folder[]>> =>
  (
    axios.get(`${this.URL}/previous/folders?id=${id}`)
      .then(response => ({ data : response.data }))
      .catch(e => ({ error : e }))
  );

  create = (folder: Folder, folderName: string) : Promise<ApiResponse<Folder>> =>
  (
    axios.post(`${this.URL}/create?name=${folderName}`, folder)
      .then(response => ({ data : response.data }))
      .catch(e => ({ error : e }))
  );

  copy = (moveData: FolderMoveData) => this.moveRequest(moveData, "copy");

  move = (moveData: FolderMoveData) => this.moveRequest(moveData, "move");

  private moveRequest = (moveData: FolderMoveData, path: string) : Promise<ApiResponse<Folder>> =>
  (
    axios.put(`${this.URL}/${path}`, moveData)
      .then(response => ({ data : response.data }))
      .catch(e => ({ error : e }))
  );
}

export default FolderClient;