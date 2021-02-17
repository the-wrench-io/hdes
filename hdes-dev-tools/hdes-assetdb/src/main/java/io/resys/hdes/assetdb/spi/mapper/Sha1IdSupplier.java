package io.resys.hdes.assetdb.spi.mapper;

/*-
 * #%L
 * hdes-object-repo
 * %%
 * Copyright (C) 2020 Copyright 2020 ReSys OÜ
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;

import io.resys.hdes.assetdb.api.AssetClient.Blob;
import io.resys.hdes.assetdb.api.AssetClient.Commit;
import io.resys.hdes.assetdb.api.AssetClient.Tree;
import io.resys.hdes.assetdb.api.AssetClient.TreeValue;
import io.resys.hdes.assetdb.api.ImmutableBlob;
import io.resys.hdes.assetdb.api.ImmutableCommit;
import io.resys.hdes.assetdb.api.ImmutableTree;
import io.resys.hdes.assetdb.spi.mapper.ObjectRepositoryMapper.IdSupplier;

@SuppressWarnings("deprecation")
public class Sha1IdSupplier implements IdSupplier {
  private static final TreeEntryComparator comparator = new TreeEntryComparator();

  @Override
  public Blob id(Blob blob) {
    String id = Hashing.sha1().hashString(blob.getValue(), Charsets.UTF_8).toString();
    return ImmutableBlob.builder().from(blob).id(id).build();
  }

  @Override
  public Tree id(Tree tree) {
    List<TreeValue> source = new ArrayList<>(tree.getValues().values());
    Collections.sort(source, comparator);
    String id = Hashing.sha1().hashString(source.toString(), Charsets.UTF_8).toString();
    return ImmutableTree.builder().from(tree).id(id).build();
  }

  @Override
  public Commit id(Commit commit) {
    String id = Hashing.sha1().hashString(commit.toString(), Charsets.UTF_8).toString();
    return ImmutableCommit.builder()
        .from(commit)
        .id(id)
        .build();
  }

  static class TreeEntryComparator implements Comparator<TreeValue> {
    @Override
    public int compare(TreeValue o1, TreeValue o2) {
      return o1.getName().compareTo(o2.getName());
    }
  }
}
