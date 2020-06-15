import React, { FC, useState, useEffect } from "react";

import { CompactSeparator } from "../CompactSeparator/CompactSeparator";

import { Theme } from "../../context/ThemeContext";

import { PropsWithTheme } from "../../models/PropsWithTheme";
import { StorageStats }   from "../../models/StorageStats";

import FolderClient from "../../api/FolderClient";

const StorageStatsView: FC<PropsWithTheme> = ({ theme }) =>
{
  const [stats, setStats] = useState<StorageStats>();
  const client = FolderClient.instance();

  useEffect(() =>
  {
    client.getRootStats().then(stats => stats && setStats(stats));
  }, [])

  const calcPercent = () : number =>
  {
    if (stats)
    {
      const { total, free } = stats;

      return (total - free) / total * 100
    }
    return 0;
  };

  return (
    <div className={`storage-stats-view`}>
      <CompactSeparator/>
      <div>
        <div>
          <span>{stats?.used || 0} / {stats?.total || 0} GB</span>
        </div>
        <div>
          <div
            style={{
              background: theme == Theme.DARK ? "#4a4a4a" : ""
            }}
            className="storage-stats-memory-bar"
          >
            <div style={{ width: `${calcPercent()}%` }}/>
          </div>
        </div>
      </div>
    </div>
  );
};

export default StorageStatsView;