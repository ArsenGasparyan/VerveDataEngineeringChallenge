# Verve Data Engineering Challenge

Project for processing mobile ad campaign data. The project calculates various metrics and recommends the top 5 advertisers based on the revenue rate.

## Business Model

Advertisement banners are displayed to users in a mobile application, identified by an `app_id`, in a specific country (identified by `country_code`), and are sourced from an advertiser (identified by `advertiser_id`). An impression event is recorded and stored whenever a banner is displayed. If the user clicks on the banner, a click event is recorded. Revenue is generated only when a click event occurs.

## Goals

1. **Read events stored in JSON files:** Read and parse the events for both impressions and clicks from the provided JSON files. Some events may not comply with the provided schema. You can use the library of your choice to perform the JSON parsing.

2. **Calculate metrics for some dimensions:** The business team wants to check how some metrics perform depending on a few dimensions. They would like to check how applications are performing depending on the country. Metrics include:
    - Count of impressions
    - Count of clicks
    - Sum of revenue

   Dimensions:
    - app_id
    - country_code

   The output should be written to a JSON file in the following format:
   ```
   [
     {
       "app_id": 1,
       "country_code": "US",
       "impressions": 102,
       "clicks": 12,
       "revenue": 10.2
     },
     ...
   ]
   ```

3. **Make a recommendation for the top 5 advertiser_ids to display for each app and country combination:** The business team wants to know which are the top advertisers for each application and country. This will allow them to focus their effort on these advertisers. To measure performance, we will check for the highest rate of revenue/impressions (i.e., the advertisers that, on average, pay more per impression).

   Output fields:
    - app_id
    - country_code
    - recommended_advertiser_ids (list of top 5 advertiser ids with the highest revenue per impression rate in this application and country)

   The output should be written to a JSON file in the following format:
   ```
   [
     {
       "app_id": 1,
       "country_code": "US",
       "recommended_advertiser_ids": [32, 12, 45, 4, 1]
     }
   ]
   ```

## Table of Contents

1. [Requirements](#requirements)
2. [Dependencies](#dependencies)
3. [Project Structure](#project-structure)
4. [Usage](#usage)

## Requirements

- Scala 2.12.10
- Apache Spark 3.3.0

## Dependencies

- Spark Core
- Spark SQL
- Typesafe Config
- Better Files
- Play JSON

## Project Structure

The project consists of the following main files:

1. `build.sbt` - The SBT configuration file containing project dependencies.
2. `MobileAdCampaign.scala` - The main class of the application, which starts the data processing.
3. `DataProcessor.scala` - The class containing the main logic for calculating metrics and recommending advertisers.

## Usage

1. Clone the repository and navigate to the project directory.

```sh 
git clone https://github.com/ArsenGasparyan/VerveDataEngineeringChallenge.git
cd VerveDataEngineeringChallenge
```

2. Build the project using SBT.

```sh
sbt clean compile
```

3. Package the project into a JAR file.

```sh
sbt package
```

4. Run the JAR file using Spark, passing in the input file paths for impressions and clicks data.

```sh
spark-submit --class verve.data.engineering.challenge.job.MobileAdCampaign target/scala-2.12/vervedataengineeringchallenge_2.12-0.1.0-SNAPSHOT.jar data/input/impressions.json data/input/clicks.json
```

5. After the data processing is completed, the output files containing calculated metrics and recommended advertiser IDs will be saved to the configured paths in `application.conf`.

## Output

The output of this project consists of two files:

1. `calculated_metrics.json` - A JSON file containing metrics like the total number of impressions, clicks, and revenue for each app and country.
2. `recommended_advertiser_ids.json` - A JSON file containing the top 5 recommended advertiser IDs for each app and country based on the revenue rate.
