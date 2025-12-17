param(
  [string[]] $ExtraArgs
)

# ...existing code...
# Script que intenta usar mvnw.cmd, si no existe usa mvn, si ninguno existe imprime instrucciones.
$projectRoot = Split-Path -Parent $MyInvocation.MyCommand.Definition
Push-Location $projectRoot
try {
  $argsToUse = if ($ExtraArgs -and $ExtraArgs.Count -gt 0) { $ExtraArgs } else { @('spring-boot:run') }

  if (Test-Path '.\mvnw.cmd') {
    Write-Output "Usando mvnw.cmd en $projectRoot..."
    & .\mvnw.cmd @argsToUse
    exit $LASTEXITCODE
  }

  if (Get-Command mvn -ErrorAction SilentlyContinue) {
    Write-Output "No se encontró mvnw.cmd. Usando 'mvn' instalado..."
    mvn @argsToUse
    exit $LASTEXITCODE
  }

  Write-Error "No se encontró ni mvnw.cmd ni el comando 'mvn'."
  Write-Output "Opciones para resolver:"
  Write-Output "  1) Instalar Apache Maven y asegurarse de que 'mvn' esté en el PATH."
  Write-Output "  2) Generar el wrapper (desde una máquina con Maven):"
  Write-Output "       mvn -N io.takari:maven:wrapper"
  Write-Output "     Esto crea mvnw, mvnw.cmd y .mvn/wrapper/* en el proyecto."
  Write-Output "  3) Copiar los archivos del wrapper (mvnw, mvnw.cmd, .mvn/wrapper) desde otro clon del proyecto."
  exit 1
} finally {
  Pop-Location
}

