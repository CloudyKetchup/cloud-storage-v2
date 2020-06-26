import { API_URL } from "./env.config";

import { StorageStats }     from "../models/StorageStats";
import { Folder }           from "../models/Directory";
import { DirectoryContent } from "../models/DirectoryContent";
import { ApiResponse, StatusResponse } from "../models/ApiResponse";

import axios from "axios";
<<<<<<< HEAD
import { FolderMoveData } from "../models/MoveData";
=======
import { DirectoryContent } from "../models/DirectoryContent";
>>>>>>> master

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

  getRoot = (onError?: () => void) : Promise<Folder | undefined> =>
  (
    axios.get(`${this.URL}/root`)
      .then(response => response.data)
      .catch(onError)
  );

  getRootStats = (onError?: () => void) : Promise<StorageStats | undefined> =>
  (
    axios.get(`${this.URL}/root/stats`)
      .then(response => response.data)
      .catch(onError)
  );

  getContent = (id: string, onError?: () => void) : Promise<DirectoryContent | undefined> =>
  (
    axios.get(`${this.URL}/content?id=${id}`)
      .then(response => response.data)
      .catch(onError)
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

	remove = (id: string) : Promise<StatusResponse> =>
	(
		axios.delete(`${this.URL}/delete?id=${id}`)
			.then(response => ({ status : response.status }))
			.catch(e => ({ error : e }))
	);
}

export default FolderClient;
