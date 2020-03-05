import json
import boto3
import datetime


def lambda_handler(event, context):
    BUCKET = "shetechimage"
    KEY = "Sad.jpeg"
    FEATURES_BLACKLIST = ("Landmarks", "Emotions", "Pose", "Quality", "BoundingBox", "Confidence")

    def detect_faces(bucket, key, attributes=['ALL']):
        rekognition = boto3.client('rekognition',
                                   region_name="ap-southeast-1")
        response = rekognition.detect_faces(
            Image={
                "S3Object": {
                    "Bucket": BUCKET,
                    "Name": KEY,
                }
            },
            Attributes=attributes,
        )
        return response['FaceDetails']

    for face in detect_faces(BUCKET, KEY):
        #       print("Face({Confidence}%)".format(**face))
        for emotion in face['Emotions']:
            if emotion['Confidence'] >= 60.0:
                emo = emotion['Type']
                print(emo)
            #    print("  {Type} : {Confidence}%".format(**emotion))
        # print(emotion['Confidence'])

    dynamodb = boto3.resource('dynamodb', region_name='ap-southeast-1')
    dynamoTable = dynamodb.Table('MorningEmotionTable')
    Primary_Column_Name = 'EmotionId'
    Primary_key = 1
    columns = ["EmailId", "Emotion", "Time"]
    dateTimeObj = datetime.datetime.now()
    timestampStr = dateTimeObj.strftime("%d-%b-%Y (%H:%M:%S.%f)")

    dynamoTable.put_item(
        Item={
            'UserId': 'abc5',
            'Date': timestampStr,
            'Emotion': emo,

        }
    )
