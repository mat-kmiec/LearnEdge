// Obsługa quizu z wyborem odpowiedzi
document.addEventListener("click", (e) => {
  if (e.target.classList.contains("course-quiz-option")) {
    const btn = e.target;
    const quizBlock = btn.closest(".course-quiz-block");
    const feedback = quizBlock.querySelector(".course-quiz-feedback");
    const isCorrect = btn.dataset.correct === "true";

    // zablokuj inne przyciski po kliknięciu
    quizBlock.querySelectorAll(".course-quiz-option").forEach((b) => {
      b.disabled = true;
      if (b.dataset.correct === "true") b.classList.add("correct");
    });

    if (isCorrect) {
      btn.classList.add("correct");
      feedback.innerHTML = `<span class="text-success fw-bold"><i class="bi bi-check-circle"></i> Dobrze! To poprawna odpowiedź.</span>`;
    } else {
      btn.classList.add("incorrect");
      feedback.innerHTML = `<span class="text-danger fw-bold"><i class="bi bi-x-circle"></i> Błędna odpowiedź!</span>`;
    }
  }
});

document.addEventListener("click", (e) => {
  if (e.target.classList.contains("check-answer-btn")) {
    const quizBlock = e.target.closest(".course-quiz-block");
    const input = quizBlock.querySelector(".course-answer-input");
    const feedback = quizBlock.querySelector(".course-quiz-feedback");

    const userAnswer = input.value.trim().toLowerCase();
    const correctAnswer = input.dataset.answer.toLowerCase();

    if (!userAnswer) {
      feedback.innerHTML = `<span class="text-warning">⚠️ Wpisz odpowiedź przed sprawdzeniem.</span>`;
      return;
    }

    if (userAnswer === correctAnswer) {
      feedback.innerHTML = `<span class="text-success fw-bold"><i class="bi bi-check-circle"></i> Dobrze! Odpowiedź poprawna.</span>`;
    } else {
      feedback.innerHTML = `<span class="text-danger fw-bold"><i class="bi bi-x-circle"></i> Błędnie! Poprawna odpowiedź to: <span class="text-info">${input.dataset.answer}</span></span>`;
    }
  }
});

document.addEventListener("click", (e) => {
  const btn = e.target.closest(".course-copy-btn");
  if (!btn) return; // kliknięcie nie dotyczy przycisku kopiowania

  const codeBlock = btn.closest(".course-code-block");
  if (!codeBlock) return;

  const code = codeBlock.querySelector("code").innerText;
  navigator.clipboard.writeText(code);

  btn.innerHTML = '<i class="bi bi-check-circle"></i> Skopiowano!';
  btn.classList.add("copied");

  setTimeout(() => {
    btn.innerHTML = '<i class="bi bi-clipboard"></i> Kopiuj';
    btn.classList.remove("copied");
  }, 2000);
});

// Sprawdzanie ćwiczenia praktycznego
document.addEventListener("click", (e) => {
  if (e.target.classList.contains("check-practice-btn")) {
    const block = e.target.closest(".course-practice-block");
    const input = block.querySelector(".practice-input");
    const feedback = block.querySelector(".course-quiz-feedback");

    const userAnswer = input.value.trim().toLowerCase();
    const correct = input.dataset.answer.trim().toLowerCase();

    if (!userAnswer) {
      feedback.innerHTML = `<span class="text-warning">⚠️ Wpisz odpowiedź przed sprawdzeniem.</span>`;
      return;
    }

    if (userAnswer === correct) {
      feedback.innerHTML = `<span class="text-success fw-bold"><i class="bi bi-check-circle"></i> Dobrze! Odpowiedź poprawna.</span>`;
      input.classList.add("correct");
    } else {
      feedback.innerHTML = `<span class="text-danger fw-bold"><i class="bi bi-x-circle"></i> Błąd! Poprawna odpowiedź to: <span class="text-info">${input.dataset.answer}</span></span>`;
      input.classList.add("incorrect");
    }
  }
});

// Tablica wszystkich elementów lekcji
const lessonBlocks = [];

// Funkcja do generowania HTML na podstawie obiektu
function generateBlockHTML(block) {
  switch (block.type) {
    case "header":
      return `
      <div>
        <h3 class="course-section-title text-start mt-0 mb-3">
          ${block.content}
        </h3>
        </div>
      `;
    case "text":
      return `
          <div class="course-text-content">
            <p>
              ${block.content}
            </p>
          </div>
      `;
    case "image":
      return `
        <div class="course-image-container my-4">
          <img
            src="${block.previewSrc}"
            alt="${block.alt}"
            class="course-image"
          />
        </div>
      `;
    case "video":
      return `
        <div class="course-video-container my-4">
          <iframe
            src="${block.src}"
            title="${block.title}"
            frameborder="0"
            allowfullscreen
          ></iframe>
        </div>
      `;
    case "code":
      return `
    <div class="course-code-block my-4">
      <div class="course-code-header d-flex justify-content-between align-items-center mb-2">
        <span><i class="bi bi-code-slash me-2"></i>${block.title}</span>
        <button class="btn btn-sm btn-outline-light course-copy-btn" onclick="copyCode(this)">
          <i class="bi bi-clipboard"></i> Kopiuj
        </button>
      </div>
      <pre><code class="language-${block.language}">${escapeHtml(
        block.code
      )}</code></pre>
    </div>
  `;
    case "quiz":
      return `
    <div class="course-quiz-block my-4">
      <h4 class="course-quiz-question mb-3">
        <i class="bi bi-pencil-square me-2"></i>${block.question}
      </h4>

      <div class="course-quiz-input d-flex align-items-center flex-wrap gap-2">
        <input
          type="text"
          class="course-answer-input form-control"
          placeholder="Wpisz swoją odpowiedź..."
          data-answer="${block.answer}"
        />
        <button class="btn btn-gradient btn-sm check-answer-btn">Sprawdź</button>
      </div>

      <div class="course-quiz-feedback mt-3"></div>
    </div>
  `;
    case "quiz-choice":
      const optionsHTML = block.options
        .map(
          (opt, i) => `
      <button class="course-quiz-option" data-correct="${
        i === block.correctIndex
      }">${opt}</button>`
        )
        .join("");

      return `
    <div class="course-quiz-block my-4">
      <h4 class="course-quiz-question mb-3">
        <i class="bi bi-question-circle me-2"></i>${block.question}
      </h4>
      <div class="course-quiz-options">
        ${optionsHTML}
      </div>
      <div class="course-quiz-feedback mt-3"></div>
    </div>
  `;
    case "practice":
      const codeWithInput = block.code.replace(
        "[___]",
        `<input type='text' class='practice-input' placeholder='...' data-answer='${block.answer}'/>`
      );

      return `
    <div class="course-practice-block my-4">
      <h4 class="course-section-title mb-1">
        <i class="bi bi-terminal me-2"></i>${block.title}
      </h4>
      <pre><code class="language-${block.language}">${codeWithInput}</code></pre>
      <button class="btn btn-gradient btn-sm check-practice-btn mt-2">Sprawdź</button>
      <div class="course-quiz-feedback mt-2"></div>
    </div>
  `;
    case "note":
      return `
    <div class="course-note my-4">
      <i class="bi bi-journal-text me-2"></i>
      <strong>Notatka:</strong> ${block.content}
    </div>
  `;
    case "tip":
      return `
    <div class="course-text-block tip mt-3 mb-3">
      <h5 class="course-text-title">
        <i class="bi bi-lightbulb me-2"></i>Wskazówka
      </h5>
      <div class="course-text-content">
        <p><strong>Pamiętaj:</strong> ${block.content}</p>
      </div>
    </div>
  `;
    case "idea":
      return `
    <div class="course-idea my-4">
      <i class="bi bi-lightbulb me-2"></i>
      <strong>Pomysł:</strong> ${block.content}
    </div>
  `;
    case "warning":
      return `
    <div class="course-warning my-4">
      <i class="bi bi-exclamation-triangle me-2 text-warning"></i>
      <strong>Uwaga:</strong> ${block.content}
    </div>
  `;
    case "fact":
      return `
    <div class="course-fact my-4">
      <i class="bi bi-info-circle me-2 text-info"></i>
      <strong>Ciekawostka:</strong> ${block.content}
    </div>
  `;
    case "audio":
      return `
    <div class="course-audio-container my-4">
      <audio controls>
        <source src="${block.src}" type="audio/mpeg" />
        Twoja przeglądarka nie obsługuje elementu audio.
      </audio>
    </div>
  `;

    default:
      return "";
  }
}

// Funkcja do odświeżania podglądu lekcji
function updateLessonPreview() {
  const previewContainer = document.getElementById("lessonPreview");
  previewContainer.innerHTML = lessonBlocks.map(generateBlockHTML).join("");
}

// Obsługa dodawania nagłówka z modala
document.getElementById("addHeaderBtn").addEventListener("click", () => {
  const input = document.getElementById("headerText");
  const value = input.value.trim();

  if (!value) {
    input.classList.add("is-invalid");
    return;
  }

  // Tworzymy obiekt typu nagłówek
  const newBlock = {
    id: Date.now(),
    type: "header",
    content: value,
  };

  // Dodajemy do tablicy
  lessonBlocks.push(newBlock);

  // Aktualizujemy podgląd
  updateLessonPreview();

  // Czyścimy input i zamykamy modal
  input.value = "";
  input.classList.remove("is-invalid");

  const modal = bootstrap.Modal.getInstance(
    document.getElementById("addHeaderModal")
  );
  modal.hide();

  console.log("Aktualne bloki lekcji:", lessonBlocks);
});

// Obsługa dodawania tekstu
document.getElementById("addTextBtn").addEventListener("click", () => {
  const textarea = document.getElementById("lessonText");
  const value = textarea.value.trim();

  if (!value) {
    textarea.classList.add("is-invalid");
    return;
  }

  // Tworzymy obiekt typu "text"
  const newBlock = {
    id: Date.now(),
    type: "text",
    content: value,
  };

  lessonBlocks.push(newBlock);
  updateLessonPreview();

  // Czyścimy pole i zamykamy modal
  textarea.value = "";
  textarea.classList.remove("is-invalid");

  const modal = bootstrap.Modal.getInstance(
    document.getElementById("addTextModal")
  );
  modal.hide();

  console.log("Aktualne bloki lekcji:", lessonBlocks);
});

// 🔹 Tablica na pliki oczekujące na upload
const pendingFiles = [];

const PLACEHOLDER_IMAGE = "../../static/img/unnamed.png"; // zawsze ta sama w podglądzie
const UPLOAD_BASE = "sciezka"; // <- bazowa ścieżka docelowa po zapisie

// Helper: slug nazwy lekcji
function slugifyLessonName() {
  const raw = document.getElementById("lessonName")?.value || "lesson";
  return raw
    .trim()
    .toLowerCase()
    .replace(/\s+/g, "_")
    .replace(/[^\w\-]+/g, "");
}

// Globalna tablica tymczasowych plików obrazów
const pendingImageFiles = [];

// 🔹 Podgląd wybranego obrazu
document.getElementById("imageFile").addEventListener("change", (e) => {
  const file = e.target.files[0];
  const previewContainer = document.getElementById("imagePreview");
  const previewImg = document.getElementById("imagePreviewImg");

  previewContainer.classList.add("d-none");

  if (!file) return;

  const ext = file.name.split(".").pop().toLowerCase();
  if (!["jpg", "jpeg", "png"].includes(ext)) {
    alert("Dozwolone są tylko pliki JPG lub PNG!");
    e.target.value = "";
    return;
  }

  const url = URL.createObjectURL(file);
  previewImg.src = url;
  previewContainer.classList.remove("d-none");
});

// 🔹 Dodanie obrazu do lekcji (bez wysyłania)
document.getElementById("addImageBtn").addEventListener("click", () => {
  const fileInput = document.getElementById("imageFile");
  const file = fileInput.files[0];

  if (!file) {
    alert("Wybierz plik JPG lub PNG!");
    return;
  }

  const ext = file.name.split(".").pop().toLowerCase();
  if (!["jpg", "jpeg", "png"].includes(ext)) {
    alert("Dozwolone są tylko pliki JPG lub PNG!");
    return;
  }

  const previewUrl = URL.createObjectURL(file);

  const newBlock = {
    id: Date.now(),
    type: "image",
    previewSrc: previewUrl, // tylko podgląd
    fileExtension: ext,
    tempFileIndex: pendingImageFiles.length, // numer pliku lokalnie
  };

  // zapisujemy plik do pamięci lokalnej
  pendingImageFiles.push(file);
  lessonBlocks.push(newBlock);

  updateLessonPreview();

  // Reset i zamknięcie modala
  fileInput.value = "";
  document.getElementById("imagePreview").classList.add("d-none");
  const modal = bootstrap.Modal.getInstance(document.getElementById("addImageModal"));
  modal.hide();
});


function updateLessonPreview() {
  const previewContainer = document.getElementById("lessonPreview");
  if (!previewContainer) return;
  previewContainer.innerHTML = lessonBlocks.map(generateBlockHTML).join("");
}

// 🔹 Obsługa modala "Dodaj wideo"
document.getElementById("addVideoBtn").addEventListener("click", () => {
  const linkInput = document.getElementById("videoLink");
  const titleInput = document.getElementById("videoTitle");

  const link = linkInput.value.trim();
  const title = titleInput.value.trim() || "Przykładowe wideo";

  if (!link) {
    alert("Podaj link do filmu YouTube!");
    return;
  }

  // Zamiana linku YouTube na format embeddable
  // np. https://www.youtube.com/watch?v=YF59k3gZeb4 → https://www.youtube.com/embed/YF59k3gZeb4
  const embedLink = link.replace("watch?v=", "embed/");

  // Tworzymy blok lekcji
  const newBlock = {
    id: Date.now(),
    type: "video",
    src: embedLink,
    title: title,
  };

  // Dodajemy do tablicy i aktualizujemy podgląd
  lessonBlocks.push(newBlock);
  updateLessonPreview();

  // Czyścimy pola i zamykamy modal
  linkInput.value = "";
  titleInput.value = "";

  const modal = bootstrap.Modal.getInstance(
    document.getElementById("addVideoModal")
  );
  modal.hide();

  console.log("Bloki lekcji:", lessonBlocks);
});

// 🔹 Obsługa modala "Dodaj blok kodu"
document.getElementById("addCodeBtn").addEventListener("click", () => {
  const title =
    document.getElementById("codeTitle").value.trim() || "Przykład kodu";
  const language = document.getElementById("codeLanguage").value || "text";
  const content = document.getElementById("codeContent").value.trim();

  if (!content) {
    alert("Wpisz lub wklej kod przed dodaniem!");
    return;
  }

  const newBlock = {
    id: Date.now(),
    type: "code",
    title,
    language,
    code: content,
  };

  lessonBlocks.push(newBlock);
  updateLessonPreview();

  // czyszczenie pól i zamknięcie modala
  document.getElementById("codeTitle").value = "";
  document.getElementById("codeLanguage").selectedIndex = 0;
  document.getElementById("codeContent").value = "";

  const modal = bootstrap.Modal.getInstance(
    document.getElementById("addCodeModal")
  );
  modal.hide();

  console.log("Bloki lekcji:", lessonBlocks);
});

function escapeHtml(text) {
  return text
    .replace(/&/g, "&amp;")
    .replace(/</g, "&lt;")
    .replace(/>/g, "&gt;")
    .replace(/"/g, "&quot;")
    .replace(/'/g, "&#039;");
}

// 🔹 Obsługa modala "Dodaj pytanie / odpowiedź"
document.getElementById("addQuestionBtn").addEventListener("click", () => {
  const question = document.getElementById("quizQuestion").value.trim();
  const answer = document.getElementById("quizAnswer").value.trim();

  if (!question || !answer) {
    alert("Wpisz pytanie i poprawną odpowiedź!");
    return;
  }

  const newBlock = {
    id: Date.now(),
    type: "quiz",
    question,
    answer,
  };

  lessonBlocks.push(newBlock);
  updateLessonPreview();

  // czyścimy i zamykamy modal
  document.getElementById("quizQuestion").value = "";
  document.getElementById("quizAnswer").value = "";
  const modal = bootstrap.Modal.getInstance(
    document.getElementById("addQuestionModal")
  );
  modal.hide();

  console.log("Bloki lekcji:", lessonBlocks);
});

// 🔹 Obsługa modala "Dodaj quiz z opcjami"
document.getElementById("addQuizBtn").addEventListener("click", () => {
  const question = document.getElementById("quizMultiQuestion").value.trim();
  const options = Array.from(document.querySelectorAll(".quiz-option-input"))
    .map((i) => i.value.trim())
    .filter((v) => v !== "");
  const correctIndex =
    parseInt(document.getElementById("quizCorrectIndex").value) - 1;

  if (!question || options.length < 2) {
    alert("Podaj pytanie i co najmniej dwie odpowiedzi!");
    return;
  }

  if (correctIndex < 0 || correctIndex >= options.length) {
    alert(
      "Numer poprawnej odpowiedzi musi mieścić się w zakresie 1–" +
        options.length
    );
    return;
  }

  const newBlock = {
    id: Date.now(),
    type: "quiz-choice",
    question,
    options,
    correctIndex,
  };

  lessonBlocks.push(newBlock);
  updateLessonPreview();

  // czyszczenie modala
  document.getElementById("quizMultiQuestion").value = "";
  document
    .querySelectorAll(".quiz-option-input")
    .forEach((i) => (i.value = ""));
  document.getElementById("quizCorrectIndex").value = 1;

  const modal = bootstrap.Modal.getInstance(
    document.getElementById("addQuizModal")
  );
  modal.hide();
});

// 🔹 Obsługa modala "Dodaj ćwiczenie praktyczne"
document.getElementById("addPracticeBtn").addEventListener("click", () => {
  const title =
    document.getElementById("practiceTitle").value.trim() ||
    "Ćwiczenie praktyczne";
  const code = document.getElementById("practiceCode").value.trim();
  const answer = document.getElementById("practiceAnswer").value.trim();
  const language = document.getElementById("practiceLang").value || "java";

  if (!code || !answer) {
    alert("Wprowadź kod z luką i poprawną odpowiedź!");
    return;
  }

  const newBlock = {
    id: Date.now(),
    type: "practice",
    title,
    code,
    answer,
    language,
  };

  lessonBlocks.push(newBlock);
  updateLessonPreview();

  // czyszczenie pól i zamknięcie modala
  document.getElementById("practiceTitle").value = "";
  document.getElementById("practiceCode").value = "";
  document.getElementById("practiceAnswer").value = "";
  document.getElementById("practiceLang").selectedIndex = 0;

  const modal = bootstrap.Modal.getInstance(
    document.getElementById("addPracticeModal")
  );
  modal.hide();

  console.log("Bloki lekcji:", lessonBlocks);
});

// 🔹 Obsługa modala "Dodaj notatkę"
document.getElementById("addNoteBtn").addEventListener("click", () => {
  const content = document.getElementById("noteContent").value.trim();

  if (!content) {
    alert("Wpisz treść notatki!");
    return;
  }

  const newBlock = {
    id: Date.now(),
    type: "note",
    content,
  };

  lessonBlocks.push(newBlock);
  updateLessonPreview();

  // reset i zamknięcie modala
  document.getElementById("noteContent").value = "";
  const modal = bootstrap.Modal.getInstance(
    document.getElementById("addNoteModal")
  );
  modal.hide();
});

// 🔹 Obsługa modala "Dodaj wskazówkę"
document.getElementById("addTipBtn").addEventListener("click", () => {
  const content = document.getElementById("tipContent").value.trim();

  if (!content) {
    alert("Wpisz treść wskazówki!");
    return;
  }

  const newBlock = {
    id: Date.now(),
    type: "tip",
    content,
  };

  lessonBlocks.push(newBlock);
  updateLessonPreview();

  // reset i zamknięcie modala
  document.getElementById("tipContent").value = "";
  const modal = bootstrap.Modal.getInstance(
    document.getElementById("addTipModal")
  );
  modal.hide();
});

// 🔹 Obsługa modala "Dodaj pomysł"
document.getElementById("addIdeaBtn").addEventListener("click", () => {
  const content = document.getElementById("ideaContent").value.trim();

  if (!content) {
    alert("Wpisz treść pomysłu!");
    return;
  }

  const newBlock = {
    id: Date.now(),
    type: "idea",
    content,
  };

  lessonBlocks.push(newBlock);
  updateLessonPreview();

  // czyszczenie i zamknięcie modala
  document.getElementById("ideaContent").value = "";
  const modal = bootstrap.Modal.getInstance(
    document.getElementById("addIdeaModal")
  );
  modal.hide();
});

// 🔹 Obsługa modala "Dodaj ostrzeżenie"
document.getElementById("addWarningBtn").addEventListener("click", () => {
  const content = document.getElementById("warningContent").value.trim();

  if (!content) {
    alert("Wpisz treść ostrzeżenia!");
    return;
  }

  const newBlock = {
    id: Date.now(),
    type: "warning",
    content,
  };

  lessonBlocks.push(newBlock);
  updateLessonPreview();

  // czyszczenie i zamknięcie modala
  document.getElementById("warningContent").value = "";
  const modal = bootstrap.Modal.getInstance(
    document.getElementById("addWarningModal")
  );
  modal.hide();
});

// 🔹 Obsługa modala "Dodaj ciekawostkę"
document.getElementById("addFactBtn").addEventListener("click", () => {
  const content = document.getElementById("factContent").value.trim();

  if (!content) {
    alert("Wpisz treść ciekawostki!");
    return;
  }

  const newBlock = {
    id: Date.now(),
    type: "fact",
    content,
  };

  lessonBlocks.push(newBlock);
  updateLessonPreview();

  // czyszczenie i zamknięcie modala
  document.getElementById("factContent").value = "";
  const modal = bootstrap.Modal.getInstance(
    document.getElementById("addFactModal")
  );
  modal.hide();
});

// globalna tablica tymczasowych plików audio
const pendingAudioFiles = [];

// 🔹 Dodawanie pliku audio (tylko pamięciowo)
document.getElementById("addAudioBtn").addEventListener("click", () => {
  const fileInput = document.getElementById("audioFile");
  const file = fileInput.files[0];

  if (!file) {
    alert("Wybierz plik MP3!");
    return;
  }

  const ext = file.name.split(".").pop().toLowerCase();
  if (ext !== "mp3") {
    alert("Dozwolone są tylko pliki MP3!");
    return;
  }

  const previewUrl = URL.createObjectURL(file);
  const newBlock = {
    id: Date.now(),
    type: "audio",
    src: previewUrl, // tylko podglądowo
    title: file.name,
    tempFileIndex: pendingAudioFiles.length, // lokalny indeks
  };

  // dodajemy do tablicy bloków i tymczasowych plików
  lessonBlocks.push(newBlock);
  pendingAudioFiles.push(file);

  updateLessonPreview();

  // reset i zamknięcie modala
  fileInput.value = "";
  document.getElementById("audioPreview").classList.add("d-none");
  const modal = bootstrap.Modal.getInstance(
    document.getElementById("addAudioModal")
  );
  modal.hide();
});


// 🔹 Funkcja generująca kompletny HTML lekcji
function generateLessonHTML() {
  // połączenie wszystkich bloków w jeden string HTML
  const html = lessonBlocks.map(generateBlockHTML).join("\n");

  // dla testu: wyświetlenie w konsoli i modalu / alercie
  console.log("📦 Wygenerowany HTML lekcji:\n", html);
  return html;
}

// 🔹 Testowy przycisk do sprawdzenia kodu HTML (np. na dole strony)
document.getElementById("previewLessonHTMLBtn")?.addEventListener("click", () => {
  const html = generateLessonHTML();

  // pokaż w modalu lub prostym oknie
  const newWindow = window.open("", "_blank");
  newWindow.document.write(`
    <html>
      <head>
        <title>Podgląd wygenerowanego HTML</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
        <style>
          body { padding: 1rem; background: #f8f9fa; }
          pre { background: #fff; border-radius: 8px; padding: 1rem; border: 1px solid #ddd; }
        </style>
      </head>
      <body>
        <h4>Wygenerowany HTML lekcji</h4>
        <pre>${escapeHtml(html)}</pre>
      </body>
    </html>
  `);
});
