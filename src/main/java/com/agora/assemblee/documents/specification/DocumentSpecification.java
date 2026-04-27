package com.agora.assemblee.documents.specification;

import com.agora.assemblee.documents.dto.DocumentSearchRequest;
import com.agora.assemblee.documents.model.Document;
import org.springframework.data.jpa.domain.Specification;

public final class DocumentSpecification {

    private DocumentSpecification() {
    }

    public static Specification<Document> build(DocumentSearchRequest request) {
        return Specification.where(keywordLike(request.getKeyword()))
                .and(equalDocumentType(request.getDocumentType()))
                .and(equalClassification(request))
                .and(equalOwnerType(request))
                .and(equalOwnerId(request))
                .and(equalPublished(request))
                .and(equalArchived(request))
                .and(equalStatus(request));
    }

    private static Specification<Document> keywordLike(String keyword) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.isBlank()) {
                return null;
            }
            String like = "%" + keyword.trim().toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("title")), like),
                    cb.like(cb.lower(root.get("documentType")), like),
                    cb.like(cb.lower(root.get("referenceNumber")), like),
                    cb.like(cb.lower(root.get("summary")), like),
                    cb.like(cb.lower(root.get("description")), like)
            );
        };
    }

    private static Specification<Document> equalDocumentType(String documentType) {
        return (root, query, cb) -> {
            if (documentType == null || documentType.isBlank()) {
                return null;
            }
            return cb.equal(cb.lower(root.get("documentType")), documentType.trim().toLowerCase());
        };
    }

    private static Specification<Document> equalClassification(DocumentSearchRequest request) {
        return (root, query, cb) -> request.getClassificationLevel() == null
                ? null
                : cb.equal(root.get("classificationLevel"), request.getClassificationLevel());
    }

    private static Specification<Document> equalOwnerType(DocumentSearchRequest request) {
        return (root, query, cb) -> request.getOwnerType() == null
                ? null
                : cb.equal(root.get("ownerType"), request.getOwnerType());
    }

    private static Specification<Document> equalOwnerId(DocumentSearchRequest request) {
        return (root, query, cb) -> request.getOwnerId() == null
                ? null
                : cb.equal(root.get("ownerId"), request.getOwnerId());
    }

    private static Specification<Document> equalPublished(DocumentSearchRequest request) {
        return (root, query, cb) -> request.getPublished() == null
                ? null
                : cb.equal(root.get("published"), request.getPublished());
    }

    private static Specification<Document> equalArchived(DocumentSearchRequest request) {
        return (root, query, cb) -> request.getArchived() == null
                ? null
                : cb.equal(root.get("archived"), request.getArchived());
    }

    private static Specification<Document> equalStatus(DocumentSearchRequest request) {
        return (root, query, cb) -> request.getStatus() == null
                ? null
                : cb.equal(root.get("status"), request.getStatus());
    }
}