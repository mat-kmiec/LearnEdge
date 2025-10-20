document.addEventListener("DOMContentLoaded", () => {
  const quizzes = document.querySelectorAll(".course-quiz-block");

  quizzes.forEach((quiz) => {
    const options = quiz.querySelectorAll(".course-quiz-option");
    const feedback = quiz.querySelector(".course-quiz-feedback");

    options.forEach((btn) => {
      btn.addEventListener("click", () => {
        // zablokuj inne odpowiedzi
        options.forEach((b) => b.classList.add("disabled"));

        const correct = btn.getAttribute("data-correct") === "true";
        if (correct) {
          btn.classList.add("correct");
          feedback.innerHTML = `<span class="text-success fw-bold">✅ Dobrze!</span>`;
        } else {
          btn.classList.add("wrong");
          feedback.innerHTML = `<span class="text-danger fw-bold">❌ Błędna odpowiedź</span>`;
        }
      });
    });
  });
});

document.addEventListener("DOMContentLoaded", () => {
  const openQuizzes = document.querySelectorAll(".course-quiz-block");

  openQuizzes.forEach((quiz) => {
    const input = quiz.querySelector(".course-answer-input");
    const button = quiz.querySelector("button");
    const feedback = quiz.querySelector(".course-quiz-feedback");

    if (!input || !button) return;

    button.addEventListener("click", () => {
      const userAnswer = input.value.trim().toLowerCase();
      const correctAnswer = input.dataset.answer.trim().toLowerCase();

      if (userAnswer === "") {
        feedback.innerHTML = `<span class="text-warning fw-bold">⚠️ Wpisz odpowiedź</span>`;
        return;
      }

      if (userAnswer === correctAnswer) {
        feedback.innerHTML = `<span class="text-success fw-bold">✅ Dobrze!</span>`;
      } else {
        feedback.innerHTML = `<span class="text-danger fw-bold">❌ Błędna odpowiedź.<br>Poprawna to: <strong>${input.dataset.answer}</strong></span>`;
      }

      // blokada pola po odpowiedzi
      input.disabled = true;
      button.disabled = true;
    });
  });
});

document.addEventListener("DOMContentLoaded", () => {
  const copyButtons = document.querySelectorAll(".course-copy-btn");

  copyButtons.forEach((btn) => {
    btn.addEventListener("click", () => {
      const code = btn
        .closest(".course-code-block")
        .querySelector("code").innerText;
      navigator.clipboard.writeText(code);

      btn.innerHTML = '<i class="bi bi-check-circle"></i> Skopiowano!';
      btn.classList.add("copied");

      setTimeout(() => {
        btn.innerHTML = '<i class="bi bi-clipboard"></i> Kopiuj';
        btn.classList.remove("copied");
      }, 2000);
    });
  });
});

document.querySelectorAll(".course-practice-block").forEach((block) => {
  const input = block.querySelector(".practice-input");
  const button = block.querySelector("button");
  const feedback = block.querySelector(".course-quiz-feedback");

  button.addEventListener("click", () => {
    if (input.value.trim().toLowerCase() === "println") {
      feedback.innerHTML = `<span class="text-success fw-bold">✅ Dobrze!</span>`;
    } else {
      feedback.innerHTML = `<span class="text-danger fw-bold">❌ Błąd. Poprawnie: <strong>println</strong></span>`;
    }
  });
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
  return raw.trim().toLowerCase().replace(/\s+/g, "_").replace(/[^\w\-]+/g, "");
}

// Dodawanie obrazu z modala
document.getElementById("addImageBtn").addEventListener("click", () => {
  const fileInput = document.getElementById("lessonImage");
  const descInput = document.getElementById("imageDescription");

  const file = fileInput.files[0];
  const alt = (descInput.value || "").trim();

  if (!file) {
    alert("Wybierz plik przed dodaniem!");
    return;
  }

  // indeks obrazów w ramach lekcji (1,2,3...)
  const imageIndex = lessonBlocks.filter(b => b.type === "image").length + 1;
  const lessonSlug = slugifyLessonName();

  // Docelowa ścieżka (po zapisie)
  // wg wymogu: "sciezka/nazwa_lekcji_indeks"
  const finalPath = `${UPLOAD_BASE}/${lessonSlug}_${imageIndex}`;

  // Do kolejki uploadu zachowujemy plik i finalną ścieżkę
  pendingFiles.push({
    originalFile: file,
    finalPath, // np. "sciezka/java_podstawy_1"
  });

  // Do podglądu: zawsze ten sam placeholder
  const newBlock = {
    id: Date.now(),
    type: "image",
    // podgląd
    previewSrc: PLACEHOLDER_IMAGE,
    alt: alt || "Obraz lekcji",
    // metadane do zapisu
    finalPath,          // "sciezka/nazwa_lekcji_indeks"
    originalName: file.name
  };

  lessonBlocks.push(newBlock);
  updateLessonPreview();

  // Reset i zamknięcie
  fileInput.value = "";
  descInput.value = "";
  const modal = bootstrap.Modal.getInstance(document.getElementById("addImageModal"));
  modal.hide();

  console.log("lessonBlocks:", lessonBlocks);
  console.log("pendingFiles:", pendingFiles);
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

  const modal = bootstrap.Modal.getInstance(document.getElementById("addVideoModal"));
  modal.hide();

  console.log("Bloki lekcji:", lessonBlocks);
});

