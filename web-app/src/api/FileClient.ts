import { API_URL } from "./env.config";

import axios from "axios";

import { File }					from "../models/Directory";
import { FileMoveData } from "../models/MoveData";
import { ApiResponse, StatusResponse } from "../models/ApiResponse";

class FileClient
{
	private static inst: FileClient | undefined; // client instance
  private URL = `${API_URL}/file`;

  private constructor() {}

  static instance = () : FileClient =>
  {
    if (!FileClient.inst)
    {
      FileClient.inst = new FileClient();
    }
    return FileClient.inst;
  };

  upload = (
    formData : FormData,
    folderId : string,
    progress?: (progress: string) => void
  ) : Promise<ApiResponse<File>> =>
  {
    const onProgress = (p: any) =>
    {
      p && progress && progress(`${(p.total - (p.total - p.loaded)) / p.total * 100}`);
    };

    return axios({
        url     : `${this.URL}/upload?folderId=${folderId}`,
        method  : "POST",
        data    : formData,
        onUploadProgress : onProgress
      })
  		.then(response => ({ data : response.data.body }))
  		.catch(e => ({ error : e }))
  };
	
	copy = (moveData: FileMoveData) => this.moveRequest(moveData, "copy");

  move = (moveData: FileMoveData) => this.moveRequest(moveData, "move");

  private moveRequest = (moveData: FileMoveData, path: string): Promise<ApiResponse<File>> =>
  (
    axios.put(`${this.URL}/${path}`, moveData)
      .then(response => ({ data : response.data }))
      .catch(e => ({ error : e }))
  );

	remove = (file: File) : Promise<StatusResponse> =>
	(
		axios({
			url : `${this.URL}/delete`,
			method : "DELETE",
			data : file
		})
			.then(response => ({ status : response.status }))
			.catch(e => ({ error : e }))
	);
}

export default FileClient;
