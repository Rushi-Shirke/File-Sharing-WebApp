package com.sharefiles.service;


import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.sharefiles.entity.FileEntity;
import com.sharefiles.exception.FileNotFoundException;
import com.sharefiles.model.FileModel;
import com.sharefiles.repository.FileRepository;

;


@Service
public class FileServiceImp implements FileService{

   
	
	@Autowired
	private FileRepository fileRepository;

	
	private FileModel convertModel(FileEntity entity) {
		FileModel model=new FileModel();
		BeanUtils.copyProperties(entity, model);
		return model;
	}

	@Override
	public List<FileModel> getAll() {
		List<FileEntity> entities=fileRepository.findAll();
		
		return entities.stream().map(this::convertModel).collect(Collectors.toList());
	}

	@Override
	public ResponseEntity<?> uplaodFile(MultipartFile file, String uploadedBy) throws IOException {
		FileEntity entity=new FileEntity();
		entity.setFileName(file.getOriginalFilename());
		entity.setUploadedBy(uploadedBy);
		entity.setExpiryTime(LocalDateTime.now().plusDays(1));
		entity.setUploadTime(LocalDateTime.now());
		entity.setFileData(file.getBytes());
		fileRepository.save(entity);
		
		return ResponseEntity.ok().body(convertModel(entity));
	}

	@Override
	public ResponseEntity<?> shareFile(int id) {
		Optional<FileEntity> entity =fileRepository.findById(id);
		if(entity.isPresent()) {
			FileEntity fileEntity=entity.get();
			FileModel fileModel=new FileModel();
			BeanUtils.copyProperties(fileEntity, fileModel);
			return ResponseEntity.ok().body(fileModel);
		}
		else {
			throw new FileNotFoundException("File Not Found");
		}
	}

	@Override
	public ResponseEntity<?> deleteFile(int id) {
		Optional<FileEntity> entity=fileRepository.findById(id);
		if(entity.isPresent()) {
			fileRepository.delete(entity.get());
			return ResponseEntity.ok().body("File Deleted Successfully");
			
		}
		else {
			throw new FileNotFoundException("File Not Found");
		}
	}

	@Override
	public ResponseEntity<?> getFile(int id) {
		Optional<FileEntity> entity=fileRepository.findById(id);
		if(entity.isPresent()) {
			FileEntity fileEntity=entity.get();
			FileModel fileModel=new FileModel();
			BeanUtils.copyProperties(fileEntity, fileModel);
			return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=\""+fileEntity.getFileName()+"\"").body(fileModel.getFileData());
			
		}
		else {
			throw new FileNotFoundException("File Not Found"); 
		}
	}

	@Override
	@Scheduled(cron="0 0 * * * *")
	public void deleteExpiredFiles() {
		List<FileEntity> expiredFiles=fileRepository.findByExpiryTimeBefore(LocalDateTime.now());
		expiredFiles.forEach(fileRepository::delete);
		System.out.println("files delted successfully"+LocalDateTime.now());
		
		
	}
	
	

}
