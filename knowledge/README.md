# Corpus

Curated texts on astrophotography, observing practice, equipment and object classes. Only documents listed in
[`manifest.json`](manifest.json) get into the index. Each entry has `id`, title, topic, object class, source, license,
version (Wikipedia revision id or date) and retrieval date. The texts are Markdown files under `<topic>/<id>.md`.

## Origin and license

- **42 articles of the English Wikipedia**, retrieved on 2026-10-05 through the MediaWiki API in the revision given in
  the manifest (`version`). Licensed under [CC BY-SA 4.0](LICENSE-CC-BY-SA-4.0.txt); the authors are the contributors
  of the respective Wikipedia article (history at the URL in `source`).
- **Changes:** The articles were normalised. Tables, infoboxes, images, references, navigation boxes and the sections
  "See also", "References", "External links" and similar were removed; formulas were replaced by their TeX source.
  The remaining text is unchanged.
- **6 own short documents** (`source` = `own`), in English like the rest, also under CC BY-SA 4.0: pixel scale and
  sampling, HOO/SHO/LRGB, dithering, calibration frames, sub-exposures and integration time, mono and one-shot colour
  cameras. They explain terms without a matching Wikipedia article.

The questions in [`../evaluation/questions.json`](../evaluation/questions.json) are German on purpose: the corpus is
English, so the embedding model has to bridge the language.
