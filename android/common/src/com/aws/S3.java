//package com.aws;
//
//import java.io.File;
//import java.util.ArrayList;
//import java.util.List;
//
//import com.amazonaws.AmazonClientException;
//import com.amazonaws.AmazonServiceException;
//import com.amazonaws.auth.BasicAWSCredentials;
//import com.amazonaws.event.ProgressEvent;
//import com.amazonaws.event.ProgressListener;
//import com.amazonaws.services.s3.AmazonS3;
//import com.amazonaws.services.s3.AmazonS3Client;
//import com.amazonaws.services.s3.model.AmazonS3Exception;
//import com.amazonaws.services.s3.transfer.Transfer.TransferState;
//import com.amazonaws.services.s3.transfer.TransferManager;
//import com.amazonaws.services.s3.transfer.Upload;
//import com.amazonaws.services.s3.transfer.model.UploadResult;
//import com.common.FilesUtils;
//import com.common.RandomUtils;
//
//public class S3 {
//
//	private BasicAWSCredentials myCredentials;
//	private TransferManager tx;
//	List<Upload> uploadingFiles = new ArrayList<Upload>();
//	private AmazonS3Client s3;
// 
//	public S3() {
//		super();
//
//		myCredentials = new BasicAWSCredentials("","");
//
//		s3 = new AmazonS3Client(myCredentials);
//
//		tx = new TransferManager(myCredentials);
//		uploadingFiles.clear();
//	}
//
//	public void upload(String bucket, File fileToUpload,
//			final uploadCallback callback) {
//
//		String key = RandomUtils.getRandomHexString(15)
//				+ FilesUtils.getFileExtions(fileToUpload.getName());
//
//		while (isFileExsist(s3, bucket, key)) {
//			key = RandomUtils.getRandomHexString(15)
//					+ FilesUtils.getFileExtions(fileToUpload.getName());
//
//		}
//
//		final Upload myUpload = tx.upload(bucket, key, fileToUpload);
//
//		myUpload.addProgressListener(new ProgressListener() {
//
//			@Override
//			public void progressChanged(ProgressEvent arg0) {
//
//				callback.onInProgress(myUpload, myUpload.getProgress()
//						.getPercentTransferred());
//
//				if (myUpload.getState() == TransferState.Waiting) {
//					callback.onWaiting(myUpload);
//				}
//
//			}
//		});
//
//		try {
//			UploadResult results = myUpload.waitForUploadResult();
//			if (myUpload.isDone()) {
//				switch (myUpload.getState()) {
//
//				case Canceled:
//
//					break;
//				case Completed:
//					callback.onCompleted(myUpload, results);
//
//					break;
//				case Failed:
//
//					break;
//				default:
//					break;
//
//				}
//			}
//		} catch (AmazonServiceException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (AmazonClientException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//	}
//
//	public static boolean isFileExsist(AmazonS3 s3, String bucketName,
//			String path) throws AmazonClientException, AmazonServiceException {
//		boolean isValidFile = true;
//		try {
//			s3.getObjectMetadata(bucketName, path);
//		} catch (AmazonS3Exception s3e) {
//			if (s3e.getStatusCode() == 404) {
//				// i.e. 404: NoSuchKey - The specified key does not exist
//				isValidFile = false;
//			} else {
//				throw s3e; // rethrow all S3 exceptions other than 404
//			}
//		}
//
//		return isValidFile;
//	}
//
//}
