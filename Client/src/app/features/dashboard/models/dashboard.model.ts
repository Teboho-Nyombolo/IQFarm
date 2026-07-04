/**
 * Summary snapshot of a crop returned by GET /api/crops/farm/{farmId}.
 * Maps the relevant fields from CropResponseDTO.
 */
export interface CropSummary {
  cropId: number;
  cropName: string;
  cropType: string;
  status: 'ACTIVE' | 'PLANNING' | 'HARVESTED' | 'FAILED';
  waterFrequency: string;
  cropCount: number;
}

/**
 * Static disease reference card shown in the Disease Monitoring section.
 * Will be replaced by live AI detection data in a future feature.
 */
export interface DiseaseReference {
  crop: string;
  disease: string;
  severity: 'HIGH ALERT' | 'MODERATE' | 'IDLE';
  description: string;
  /** Path relative to /assets/ */
  imageSrc: string;
}
