{{- define "sdsb.name" -}}
{{- default .Chart.Name .Values.nameOverride | trunc 63 | trimSuffix "-" -}}
{{- end -}}

{{- define "sdsb.fullname" -}}
{{- $name := default .Chart.Name .Values.nameOverride -}}
{{- printf "%s-%s" .Release.Name $name | trunc 63 | trimSuffix "-" -}}
{{- end -}}

{{- define "sdsb.labels" -}}
helm.sh/chart: {{ printf "%s-%s" .Chart.Name .Chart.Version | replace "+" "_" | trunc 63 | trimSuffix "-" }}
{{ include "sdsb.selectorLabels" . }}
app.kubernetes.io/version: {{ .Chart.AppVersion | quote }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
{{- end -}}

{{- define "sdsb.selectorLabels" -}}
app.kubernetes.io/name: {{ include "sdsb.name" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
{{- end -}}

{{- define "sdsb.serviceAccountName" -}}
{{- if .Values.serviceAccount.create -}}
{{- default (include "sdsb.fullname" .) .Values.serviceAccount.name -}}
{{- else -}}
{{- default "default" .Values.serviceAccount.name -}}
{{- end -}}
{{- end -}}

{{- define "sdsb.mysql.fullname" -}}
{{- printf "%s-mysql" (include "sdsb.fullname" .) | trunc 63 | trimSuffix "-" -}}
{{- end -}}

{{- define "sdsb.db.host" -}}
{{- if .Values.mysql.enabled -}}
{{- include "sdsb.mysql.fullname" . -}}
{{- else -}}
{{- required "externalDatabase.host is required when mysql.enabled=false" .Values.externalDatabase.host -}}
{{- end -}}
{{- end -}}

{{- define "sdsb.db.port" -}}
{{- if .Values.mysql.enabled -}}3306{{- else -}}{{ .Values.externalDatabase.port }}{{- end -}}
{{- end -}}

{{- define "sdsb.jdbcUrl" -}}
{{- printf "jdbc:mysql://%s:%s/%s" (include "sdsb.db.host" .) (include "sdsb.db.port" .) .Values.app.db.database -}}
{{- end -}}
