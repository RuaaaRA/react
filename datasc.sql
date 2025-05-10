--
-- PostgreSQL database dump
--

-- Dumped from database version 17.4
-- Dumped by pg_dump version 17.4

-- Started on 2025-03-30 16:50:05

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- TOC entry 4987 (class 0 OID 35185)
-- Dependencies: 246
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.users (id, first_name, last_name, password, provider, role, username) FROM stdin;
1	John	Doe	password123	LOCAL	ADMIN	johndoe
2	Jane	Smith	password123	LOCAL	INSTRUCTOR	janesmith
3	Alice	Johnson	password123	GOOGLE	STUDENT	alicej
9	Johcdn	Ddwoe	passwdord123	LOCAL	ADMIN	johnfvddoe
10	Jane	Smith	password123	LOCAL	INSTRUCTOR	janesfecmith
11	Alice	Johnson	password123	GOOGLE	STUDENT	alicefredj
13	John	Doe	password123	LOCAL	ADMIN	johndefebgrerfoe
14	Jane	Smith	password123	LOCAL	INSTRUCTOR	janesgeremith
15	Alice	Johnson	password123	GOOGLE	STUDENT	aliceergrej
16	fJيn	Dffbwdfde	$2a$10$EMnsihDpW0DBviSu81ZajenUMQd1366U.vfoyoOWcmugepTOJYi92	\N	ADMIN	fبfCcDيnFCCffdsؤfcff
17	fJيn	Dffbwdfde	$2a$10$r3/ttFPbFV/tuRO8DCTw3ufkC7pDcgr.aPN1fbDfjXV9EIjh1K0u2	\N	INSTRUCTOR	fبfCcDvvيnFCfvCffdsؤfcff
18	JoRVFDhn	DoGEWGWe	passwGGord123	LOCAL	STUDENT	joGGhn.doe
19	JanTRRe	SmiGth	passGword456	LOCAL	STUDENT	jane.GGsmith
20	AlGEWi	KhGan	passwoGrd789	LOCAL	STUDENT	ali.kGGhan
21	fJيn	Dffbwdfde	$2a$10$qqeu5RzWNKzV2nqquCu.l.5jvUZ7yniSo6RBjqet1ei93nG0YDgUy	\N	INSTRUCTOR	fبfCcDvvيnFCfvCffdFFsؤfcff
22	fJققVقيn	DffbBFDييfde	$2a$10$nqxLD0EjYE8fsY8DLfL1mu4XQpCsdrDjlpwZxbLysNH5/J9fnwPW2	\N	ADMIN	fبfCcDقققsaxيHDFnFCCfؤfcff
23	fJfيn	Dffbfwdfde	$2a$10$vdYRkW13WZs82Nk0cqCG5O3rPw.aks3iPAYgj70hNlRUypJ4ZIG2W	\N	INSTRUCTOR	fبfCcDvvيnffFCfvCffdFFsؤfcff
\.


--
-- TOC entry 4964 (class 0 OID 35100)
-- Dependencies: 223
-- Data for Name: courses; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.courses (duration, id, instructor_id, description, title) FROM stdin;
6	1	2	Course on Data Science	Data Science 101
4	2	2	Introductory course to Python	Python Basics
4	4	2	Introductory course to Python	Python Basics
1073741824	5	\N	string	string
1073741824	6	\N	string	string
6	3	17	Course on Data Science	Data Science 101
1073741824	7	10	string	string
\.


--
-- TOC entry 4979 (class 0 OID 35155)
-- Dependencies: 238
-- Data for Name: quiz; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.quiz (course_id, id, description, title) FROM stdin;
1	1	Quiz on Data Science concepts	Data Science Quiz 1
2	2	Basic Python programming quiz	Python Quiz 1
2	4	Basic Python programming quiz	Python Quiz 1
4	5	string	string
\.


--
-- TOC entry 4976 (class 0 OID 35143)
-- Dependencies: 235
-- Data for Name: question; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.question (id, quiz_id, correct_answer, difficulty, question_type, text) FROM stdin;
1	1	A	Medium	MULTIPLE_CHOICE	What is Data Science?
2	2	B	Easy	MULTIPLE_CHOICE	What does Python do?
3	1	A	Medium	MULTIPLE_CHOICE	What is Data Science?
4	2	B	Easy	MULTIPLE_CHOICE	What does Python do?
5	5	Paris	\N	MULTIPLE_CHOICE	\N
\.


--
-- TOC entry 4959 (class 0 OID 35083)
-- Dependencies: 218
-- Data for Name: answer; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.answer (id, question_id, quiz_id) FROM stdin;
1	1	1
2	2	2
3	1	1
4	2	2
\.


--
-- TOC entry 4960 (class 0 OID 35088)
-- Dependencies: 219
-- Data for Name: answer_texts; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.answer_texts (answer_id, answer_text) FROM stdin;
1	A) Analysis of data
2	B) Programming language
1	A) Analysis of data
2	B) Programming language
\.


--
-- TOC entry 4962 (class 0 OID 35092)
-- Dependencies: 221
-- Data for Name: assignment; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.assignment (max_score, course_id, due_date, id, description, title) FROM stdin;
50	2	2025-03-30 23:59:59	2	Solve database queries.	Database Assignment
75	4	2025-04-05 23:59:59	3	Implement a REST API using Spring Boot.	REST API Assignment
90	3	2025-05-01 23:59:59	4	Develop a frontend using React.	Frontend Development Assignment
\.


--
-- TOC entry 4966 (class 0 OID 35108)
-- Dependencies: 225
-- Data for Name: enrollments; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.enrollments (completed, enrolled_at, course_id, id, student_id) FROM stdin;
t	2025-03-25	1	1	3
f	2025-03-26	2	2	3
t	2025-03-25	1	3	3
f	2025-03-26	2	4	3
f	2025-03-30	1	6	11
f	2025-03-30	1	7	15
f	2025-03-30	1	8	17
t	2025-03-30	2	9	17
t	2025-03-30	7	10	17
f	2025-03-30	3	11	17
f	2025-03-30	4	12	17
f	2025-03-30	3	14	3
t	2025-03-30	2	13	21
f	2025-03-30	3	16	19
t	2025-03-30	3	15	21
\.


--
-- TOC entry 4968 (class 0 OID 35114)
-- Dependencies: 227
-- Data for Name: grade; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.grade (score, id, quiz_id, student_id) FROM stdin;
90	1	1	3
80	2	2	3
90	3	1	3
80	4	2	3
0	5	1	1
\.


--
-- TOC entry 4970 (class 0 OID 35120)
-- Dependencies: 229
-- Data for Name: lesson; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.lesson (course_id, id, audio_url, description, pdf_url, title, video_url) FROM stdin;
1	1	audio_url_1.mp3	Introduction to Data Science	pdf_url_1.pdf	Data Science Lecture 1	video_url_1.mp4
2	2	audio_url_2.mp3	Introduction to Python	pdf_url_2.pdf	Python Basics Lecture 1	video_url_2.mp4
1	3	audio_url_1.mp3	Introduction to Data Science	pdf_url_1.pdf	Data Science Lecture 1	video_url_1.mp4
2	4	audio_url_2.mp3	Introduction to Python	pdf_url_2.pdf	Python Basics Lecture 1	video_url_2.mp4
1	6	https://example.com/audio.mp3	This lesson covers the basics of Spring Boot.	https://example.com/lesson.pdf	Introduction to Spring Boot	https://example.com/video.mp4
1	7	https://example.com/audio.mp3	This lesson covers the basics of Spring Boot.	https://example.com/lesson.pdf	Introduction to Spring Boot	https://example.vvvcom/video.mp4
\.


--
-- TOC entry 4972 (class 0 OID 35128)
-- Dependencies: 231
-- Data for Name: lesson_progress; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.lesson_progress (completed, id, lesson_id, student_username) FROM stdin;
t	1	1	alicej
f	2	2	alicej
t	3	1	alicej
f	4	2	alicej
t	5	1	fبfCcDvvيnFCfvCffdsؤfcff
\.


--
-- TOC entry 4974 (class 0 OID 35134)
-- Dependencies: 233
-- Data for Name: lesson_resource; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.lesson_resource (id, lesson_id, title, type, url) FROM stdin;
1	1	Data Science PDF	PDF	http://example.com/data_science.pdf
2	2	Python Video	VIDEO	http://example.com/python_video.mp4
3	1	Data Science PDF	PDF	http://example.com/data_science.pdf
4	2	Python Video	VIDEO	http://example.com/python_video.mp4
\.


--
-- TOC entry 4977 (class 0 OID 35151)
-- Dependencies: 236
-- Data for Name: question_options; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.question_options (question_id, options) FROM stdin;
1	A) Analysis of data
1	B) Cooking data
1	C) Writing code
1	D) Cleaning data
2	A) Draw shapes
2	B) Programming language
2	C) Play games
2	D) Internet browser
1	A) Analysis of data
1	B) Cooking data
1	C) Writing code
1	D) Cleaning data
2	A) Draw shapes
2	B) Programming language
2	C) Play games
2	D) Internet browser
5	Paris
5	London
5	Berlin
5	Rome
\.


--
-- TOC entry 4981 (class 0 OID 35163)
-- Dependencies: 240
-- Data for Name: student_lesson_progress; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.student_lesson_progress (completed, completed_at, id, lesson_id, student_id) FROM stdin;
\.


--
-- TOC entry 4983 (class 0 OID 35169)
-- Dependencies: 242
-- Data for Name: system_settings; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.system_settings (id, key, value) FROM stdin;
\.


--
-- TOC entry 4985 (class 0 OID 35177)
-- Dependencies: 244
-- Data for Name: tokens; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.tokens (id, logged_out, user_id, access_token, refresh_token) FROM stdin;
1	f	16	eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiQURNSU4iLCJzdWIiOiJm2KhmQ2NE2YpuRkNDZmZkc9ikZmNmZiIsImlhdCI6MTc0MzMzNjk0NSwiZXhwIjoxNzQzNDIzMzQ1fQ.4Fp4GYLYOWrmQef8fdAqh0SqqAr_WVJKe_cKErNNWgQ	eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiQURNSU4iLCJzdWIiOiJm2KhmQ2NE2YpuRkNDZmZkc9ikZmNmZiIsImlhdCI6MTc0MzMzNjk0NSwiZXhwIjoxNzQzOTQxNzQ1fQ.AsuTJluZV5or_TyQ8PSEjqjJS0etnhhYzPXhBEJSXgU
2	f	17	eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiSU5TVFJVQ1RPUiIsInN1YiI6ImbYqGZDY0R2dtmKbkZDZnZDZmZkc9ikZmNmZiIsImlhdCI6MTc0MzMzNzA1OSwiZXhwIjoxNzQzNDIzNDU5fQ.oo6xfbzZ226YcM67DX0W3UNR_aMoPlAYzabFSBuqVCs	eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiSU5TVFJVQ1RPUiIsInN1YiI6ImbYqGZDY0R2dtmKbkZDZnZDZmZkc9ikZmNmZiIsImlhdCI6MTc0MzMzNzA1OSwiZXhwIjoxNzQzOTQxODU5fQ.VKRpx62uzwDonlTSL79v4ViXwArjS0KF6rVNCLfjXlY
3	f	21	eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiSU5TVFJVQ1RPUiIsInN1YiI6ImbYqGZDY0R2dtmKbkZDZnZDZmZkRkZz2KRmY2ZmIiwiaWF0IjoxNzQzMzQzNjQ2LCJleHAiOjE3NDM0MzAwNDZ9.1zLhKfAAGzcAwQx3YYAm3rtMSzMT5GclHu6po_gle6Q	eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiSU5TVFJVQ1RPUiIsInN1YiI6ImbYqGZDY0R2dtmKbkZDZnZDZmZkRkZz2KRmY2ZmIiwiaWF0IjoxNzQzMzQzNjQ2LCJleHAiOjE3NDM5NDg0NDZ9.pcuIpZSic_TsdCqtoJngp9jOznBvF1GFOuFsF6Dll_g
4	f	22	eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiQURNSU4iLCJzdWIiOiJm2KhmQ2NE2YLZgtmCc2F42YpIREZuRkNDZtikZmNmZiIsImlhdCI6MTc0MzM0NDQxMSwiZXhwIjoxNzQzNDMwODExfQ.AU7fL8eCy8ZwxJOJuIpy-OwHTSdbjEdulKDn8abv3gI	eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiQURNSU4iLCJzdWIiOiJm2KhmQ2NE2YLZgtmCc2F42YpIREZuRkNDZtikZmNmZiIsImlhdCI6MTc0MzM0NDQxMSwiZXhwIjoxNzQzOTQ5MjExfQ._nvu1UtXGcXQht_qUL__PB-sSJH6DxAbcPQmC9g3x08
5	f	22	eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiQURNSU4iLCJzdWIiOiJm2KhmQ2NE2YLZgtmCc2F42YpIREZuRkNDZtikZmNmZiIsImlhdCI6MTc0MzM0NDQ1NSwiZXhwIjoxNzQzNDMwODU1fQ.kwBnHXujw5KizHbY5ILPfIhzHjC2WlickCkstAHdR7A	eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiQURNSU4iLCJzdWIiOiJm2KhmQ2NE2YLZgtmCc2F42YpIREZuRkNDZtikZmNmZiIsImlhdCI6MTc0MzM0NDQ1NSwiZXhwIjoxNzQzOTQ5MjU1fQ.65gXWl3iNH2uXSDIy7IEnguGJaMu-2PskVDOUrMqC7c
6	f	23	eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiSU5TVFJVQ1RPUiIsInN1YiI6ImbYqGZDY0R2dtmKbmZmRkNmdkNmZmRGRnPYpGZjZmYiLCJpYXQiOjE3NDMzNDU0ODQsImV4cCI6MTc0MzQzMTg4NH0.1qeu-yz-TvT9ZarKCLY72XmDeifGlGW9kNBIURe-KyY	eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiSU5TVFJVQ1RPUiIsInN1YiI6ImbYqGZDY0R2dtmKbmZmRkNmdkNmZmRGRnPYpGZjZmYiLCJpYXQiOjE3NDMzNDU0ODQsImV4cCI6MTc0Mzk1MDI4NH0.kGMQskUCYihCu_rJnrlNHOAjkfE060CFF_2NK-66_eI
\.


--
-- TOC entry 4993 (class 0 OID 0)
-- Dependencies: 217
-- Name: answer_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.answer_id_seq', 4, true);


--
-- TOC entry 4994 (class 0 OID 0)
-- Dependencies: 220
-- Name: assignment_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.assignment_id_seq', 4, true);


--
-- TOC entry 4995 (class 0 OID 0)
-- Dependencies: 222
-- Name: courses_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.courses_id_seq', 7, true);


--
-- TOC entry 4996 (class 0 OID 0)
-- Dependencies: 224
-- Name: enrollments_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.enrollments_id_seq', 16, true);


--
-- TOC entry 4997 (class 0 OID 0)
-- Dependencies: 226
-- Name: grade_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.grade_id_seq', 5, true);


--
-- TOC entry 4998 (class 0 OID 0)
-- Dependencies: 228
-- Name: lesson_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.lesson_id_seq', 7, true);


--
-- TOC entry 4999 (class 0 OID 0)
-- Dependencies: 230
-- Name: lesson_progress_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.lesson_progress_id_seq', 5, true);


--
-- TOC entry 5000 (class 0 OID 0)
-- Dependencies: 232
-- Name: lesson_resource_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.lesson_resource_id_seq', 4, true);


--
-- TOC entry 5001 (class 0 OID 0)
-- Dependencies: 234
-- Name: question_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.question_id_seq', 5, true);


--
-- TOC entry 5002 (class 0 OID 0)
-- Dependencies: 237
-- Name: quiz_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.quiz_id_seq', 5, true);


--
-- TOC entry 5003 (class 0 OID 0)
-- Dependencies: 239
-- Name: student_lesson_progress_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.student_lesson_progress_id_seq', 1, false);


--
-- TOC entry 5004 (class 0 OID 0)
-- Dependencies: 241
-- Name: system_settings_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.system_settings_id_seq', 1, false);


--
-- TOC entry 5005 (class 0 OID 0)
-- Dependencies: 243
-- Name: tokens_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.tokens_id_seq', 6, true);


--
-- TOC entry 5006 (class 0 OID 0)
-- Dependencies: 245
-- Name: users_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.users_id_seq', 23, true);


-- Completed on 2025-03-30 16:50:06

--
-- PostgreSQL database dump complete
--

