package com.kma_backend.kma_backend.image;

import com.kma_backend.kma_backend.note.Note;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "images")
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Det riktiga filnamnet (valfritt)
    private String fileName;

    // URL till bilden hos Cloudinary
    private String url;

    // Cloudinary public_id, behövs för att kunna radera bilden från Cloudinary
    private String publicId;

    @ManyToOne(fetch = FetchType.LAZY)
    private Note note;
}

