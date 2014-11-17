package com.gu.mediaservice.lib.cleanup

import com.gu.mediaservice.model.ImageMetadata

trait MetadataCleaner {
  def clean(metadata: ImageMetadata): ImageMetadata
}

class MetadataCleaners(creditBylineMap: Map[String, List[String]]) {

  val attrCreditFromBylineCleaners = creditBylineMap.map { case (credit, bylines) =>
    AttributeCreditFromByline(bylines, credit)
  }

  val allCleaners: List[MetadataCleaner] = List(
    CleanRubbishLocation,
    StripCopyrightPrefix,
    UseCanonicalGuardianCredit,
    ExtractGuardianCreditFromByline
  ) ++ attrCreditFromBylineCleaners ++ List(
    StripBylineFromCredit,
    CountryCode,
    CapitaliseByline,
    CapitaliseCountry,
    CapitaliseState,
    CapitaliseCity,
    CapitaliseSubLocation,
    DropRedundantTitle
  )

  def clean(inputMetadata: ImageMetadata): ImageMetadata =
    allCleaners.foldLeft(inputMetadata) {
      case (metadata, cleaner) => cleaner.clean(metadata)
    }
}

// e.g.
// BERLIN, BERLIN - JULY 28: Visitors dance on the dancefloor to the music of a dj in the club 'Watergate' in Berlin Kreuzberg district on July 28, 2012 in Berlin, Germany. (Photo by Carsten Koall/Getty Images)
// COLOMBO, SRI LANKA - DECEMBER 16: Jos Buttler of England bats during the 7th One Day International match between Sri Lanka and England at R. Premadasa Stadium on December 16, 2014 in Colombo, Sri Lanka. (Photo by Gareth Copley/Getty Images,)
// epa04524415 Australian players celebrate a goal during the 2014 Men's Field Hockey Champions Trophy quarter final match between Argentina and Australia in Bhubaneswar, India, 11 December 2014. Australia won 4-2. EPA/HARISH TYAGI
// epa04531269 Pakistani people hold placards during a protest against an attack at the Army run school, in Islamabad Pakistan, 16 December 2014. Pakistani commandos are fighting Taliban militants who have killed at least 129 people and taken hundreds of students and teachers hostage at a military-run school, officials said. 'The operation is under way' in the north-western city of Peshawar, said Pervaiz Khattak, chief minister of Khyber-Pakhtunkhwa province. 'Intense gun fighting is taking place inside the school.' The area was cordoned off and helicopters were flying overhead. EPA/T. MUGHAL
object StripDescriptionLocationTimePrefix extends MetadataCleaner {
  def clean(metadata: ImageMetadata): ImageMetadata = {
    metadata.copy(description = metadata.description.map(stripPrefix(metadata)))
  }

  def stripPrefix(metadata: ImageMetadata)(description: String): String = {
    val location = List(metadata.city, metadata.country).flatMap(_.toList).mkString(", ") // FIXME: or city again?
    val date = metadata.dateTaken.toString
    val prefix = s"${location.toUpperCase} - ${date.toString.toUpperCase}: (.*)".r
    val epaPrefix = "epa(\\d+) (.*)".r
    description match {
      case prefix(desc) => desc
        // FIXME: epaId is Unique Document Identifier
      case epaPrefix(epaId, desc) => desc
      case desc => desc
    }
  }
}


// e.g.
// BERLIN, BERLIN - JULY 28: Visitors dance on the dancefloor to the music of a dj in the club 'Watergate' in Berlin Kreuzberg district on July 28, 2012 in Berlin, Germany. (Photo by Carsten Koall/Getty Images)
// COLOMBO, SRI LANKA - DECEMBER 16: Jos Buttler of England bats during the 7th One Day International match between Sri Lanka and England at R. Premadasa Stadium on December 16, 2014 in Colombo, Sri Lanka. (Photo by Gareth Copley/Getty Images,)
// epa04524415 Australian players celebrate a goal during the 2014 Men's Field Hockey Champions Trophy quarter final match between Argentina and Australia in Bhubaneswar, India, 11 December 2014. Australia won 4-2. EPA/HARISH TYAGI
// epa04531269 Pakistani people hold placards during a protest against an attack at the Army run school, in Islamabad Pakistan, 16 December 2014. Pakistani commandos are fighting Taliban militants who have killed at least 129 people and taken hundreds of students and teachers hostage at a military-run school, officials said. 'The operation is under way' in the north-western city of Peshawar, said Pervaiz Khattak, chief minister of Khyber-Pakhtunkhwa province. 'Intense gun fighting is taking place inside the school.' The area was cordoned off and helicopters were flying overhead. EPA/T. MUGHAL
// Eddie Howe the Bournemouth FC manager during a training session at Kings Park next to the Goldsands Stadium on December 15th 2014 in Bournemouth (Photo by Tom Jenkins)
// A view is seen of the Petrobras headquarter in Rio de Janeiro December 16, 2014. Concern over the corruption scandal is driving shares and bonds of Petrobras to multi-year lows. Preferred shares of the Rio de Janeiro-based company fell 9.1 percent on Monday, touching their lowest level since 2005. REUTERS/Sergio Moraes (BRAZIL - Tags: ENERGY CRIME LAW CIVIL UNREST)
// England's captain Alastair Cook reacts as he walks off the field after his dismissal by Sri Lanka's Suranga Lakmal during their final ODI (One Day International) cricket match in Colombo December 16, 2014.REUTERS/Dinuka Liyanawatte (SRI LANKA - Tags: SPORT CRICKET)
// Servicemen shout during a farewell ceremony at the Vaziani military base close to the Georgian capital Tbilisi on December 16, 2014. According to Georgia's Defence Ministry, the selected company of IV mechanized brigade of the Georgian Armed Forces is departing to Afghanistan to carry out international mission. AFP PHOTO / VANO SHLAMOVVANO SHLAMOV/AFP/Getty Images
// The Governor of the Bank of England, Mark Carney speaks during the Bank of England's Financial Stability Report press conference at the Bank of England in London, Tuesday Dec. 16, 2014. Consumer price inflation in the U.K. fell to a 12-year low of 1 percent in the year to November as the slide in oil prices diminished price pressures, according to official figures released Tuesday. (AP Photo / pool)
object StripDescriptionCreditSuffix extends MetadataCleaner {
  def clean(metadata: ImageMetadata): ImageMetadata = {
    metadata.copy(description = metadata.description.map(stripSuffix(metadata)))
  }

  def stripSuffix(metadata: ImageMetadata)(description: String): String = {
    val byline = metadata.byline getOrElse ""
    val credit = metadata.credit getOrElse ""
    description.replaceFirst(s"\\s+(Photo by $byline(/$credit)?[.,]*)\\s+ $$", "")
  }
}


// By vague order of importance:

// TODO: strip location+date prefix from description
// TODO: strip credit suffix from description
// TODO: strip (extract?) country + tags suffix from description

// TODO: strip (?) numbers or crappy acronyms as byline
// TODO: multiple country names (SWITZERLAND SCHWEIZ SUISSE, HKG, CHN) to clean name

// TODO: ignore crappy "keywords" (:rel:d:bm:LM1EAAO112401)

// TODO: unique keywords

// Ingested metadata:

// TODO: record Date Created or Date/Time Original
// TODO: ignore Unknown tags from fileMetadata

// TODO: artist (vs byline)?
