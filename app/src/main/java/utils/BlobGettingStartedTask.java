package utils; /**
 * Copyright Microsoft Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.util.UUID;

import android.app.Activity;
import android.database.CursorJoiner;
import android.os.AsyncTask;
import android.widget.TextView;

import com.dissertation.findtheclue.MainActivity;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.*;

import org.json.JSONObject;

/**
 * This sample illustrates basic usage of the various Blob Primitives provided
 * in the Storage Client Library including CloudBlobContainer, CloudBlockBlob
 * and CloudBlobClient.
 */
public class BlobGettingStartedTask extends AsyncTask<String, Void, Void> {

    private OnTaskCompleted listener;
    private String imgName;
    private String imgPath;
    private JSONObject jsonObject;
    public final String storageConnectionString = "DefaultEndpointsProtocol=https;AccountName=findthecluestorage;AccountKey=RhbkPO20TwB0VOm/gkcUXwK/KM+4gbXp74mw8so/rszyi0/Y1fF3cnMyvIgZ5PBCN6qRXL0cbEBrcJIbkSbK8g==;EndpointSuffix=core.windows.net";

    public BlobGettingStartedTask(String name, String path, JSONObject jsonObject, OnTaskCompleted listener) {
        this.imgName = name;
        this.imgPath = path;
        this.jsonObject = jsonObject;
        this.listener = listener;
    }

    @Override
    protected Void doInBackground(String... arg0) {

        //act.printSampleStartInfo("BlobBasics");

        try {
            // Setup the cloud storage account.
            CloudStorageAccount account = CloudStorageAccount
                    .parse(storageConnectionString);

            // Create a blob service client
            CloudBlobClient blobClient = account.createCloudBlobClient();

            // Get a reference to a container
            // The container name must be lower case
            // Append a random UUID to the end of the container name so that
            // this sample can be run more than once in quick succession.
            CloudBlobContainer container = blobClient.getContainerReference("imagescontainer");

            // Create the container if it does not exist
            container.createIfNotExists();

            // Make the container public
            // Create a permissions object
            BlobContainerPermissions containerPermissions = new BlobContainerPermissions();

            // Include public access in the permissions object
            containerPermissions
                    .setPublicAccess(BlobContainerPublicAccessType.CONTAINER);

            // Set the permissions on the container
            container.uploadPermissions(containerPermissions);

            // Upload 3 blobs
            // Get a reference to a blob in the container
            CloudBlockBlob blob1 = container
                    .getBlockBlobReference(this.imgName);

            jsonObject.put("PictureUrl", blob1.getUri());

            // Upload text to the blob
            blob1.uploadFromFile(this.imgPath);

        } catch (Throwable t) {
            //act.printException(t);
        }

       // act.printSampleCompleteInfo("BlobBasics");

        return null;
    }

    @Override
    protected void onPostExecute(Void success) {
        // your stuff
        listener.onTaskCompleted(jsonObject);
    }
}